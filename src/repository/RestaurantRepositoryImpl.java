package repository;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import domain.Restaurant;
import domain.exceptions.DuplicateIdException;
import domain.exceptions.ResourceNotFoundException;

/*
    Comments:

    - Negative ID represents logical Deletion. (gravestone)
    - Date persistence: milliseconds since the Unix epoch. [January 1, 1970, 00:00:00 GMT]
*/

public class RestaurantRepositoryImpl implements RestaurantRepository, AutoCloseable {

    private final File file;
    private final RandomAccessFile raf;
    private final int LENGTH_STRING_FIXED;

    public RestaurantRepositoryImpl(String binaryFilePath, int LENGTH_STRING_FIXED)
            throws IOException {
        this.LENGTH_STRING_FIXED = LENGTH_STRING_FIXED;
        this.file = new File(binaryFilePath);
        if (!this.file.exists()) {
            this.file.createNewFile();
        }
        this.raf = new RandomAccessFile(file, "rw");
        writeLastAddedId(-1);
    }

    @Override
    public void save(Restaurant restaurant) throws Exception {
        int lastAddedId = readLastAddedId();
        if (restaurant.getId() >= 0 && restaurant.getId() <= lastAddedId) {
            throw new DuplicateIdException(restaurant.getId());
        } else if (restaurant.getId() < 0) {
            restaurant.setId(++lastAddedId);
        }
        try {
            short recordSize = getRecordLength(restaurant);
            persistRecordWithSize(restaurant, recordSize, file.length());
            writeLastAddedId(restaurant.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void persistRecordWithSize(Restaurant restaurant, short size, long address) throws IOException {
        raf.seek(address);
        raf.writeShort(size);
        writeRecorInStream(raf, restaurant);
    }

    @Override
    public Optional<Restaurant> findById(int id) throws Exception {
        raf.seek(0);
        raf.skipBytes(Integer.BYTES);
        while (true) {
            try {
                long currPointer = raf.getFilePointer();
                short currRecordSize = raf.readShort();
                int currId = raf.readInt();

                if (currId == id) {
                    long recordStartPosition = currPointer + Short.BYTES;
                    raf.seek(recordStartPosition);
                    Restaurant found = readRestaurantFromStream(raf);
                    return Optional.of(found);
                } else {
                    long nextOffset = currPointer + currRecordSize + Short.BYTES;
                    raf.seek(nextOffset);
                }
            } catch (EOFException e) {
                break;
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Restaurant updatedRestaurant) throws Exception {
        raf.seek(0);
        raf.skipBytes(Integer.BYTES);
        boolean found = false;
        while (true) {
            try {
                long pointer = raf.getFilePointer();
                short recordSize = raf.readShort();
                int currentId = raf.readInt();

                if (currentId == updatedRestaurant.getId()) {
                    found = true;
                    long recordStartPosition = pointer + Short.BYTES;
                    raf.seek(recordStartPosition);

                    short updatedRecordSize = getRecordLength(updatedRestaurant);
                    if (recordSize == updatedRecordSize) {
                        persistRecordWithSize(updatedRestaurant, updatedRecordSize, recordStartPosition);
                    } else {
                        raf.writeInt(-1);
                        persistRecordWithSize(updatedRestaurant, updatedRecordSize,  file.length());
                    }

                    break;
                } else {
                    long nextOffset = pointer + recordSize + Short.BYTES;
                    raf.seek(nextOffset);
                }
            } catch (EOFException e) {
                break;
            }
        }

        return found;
    }

    @Override
    public void delete(int id) throws Exception {
        raf.seek(0);
        raf.skipBytes(Integer.BYTES);
        boolean found = false;
        while (true) {
            try {
                long pointer = raf.getFilePointer();
                short recordSize = raf.readShort();
                int currentId = raf.readInt();

                if (currentId == id) {
                    long recordStartPosition = pointer + Short.BYTES;
                    raf.seek(recordStartPosition);

                    raf.writeInt(-1);
                    found = true;
                    break;
                } else {
                    long nextOffset = pointer + recordSize + Short.BYTES;
                    raf.seek(nextOffset);
                }
            } catch (EOFException e) {
                break;
            }
        }
        if (!found) {
            throw new ResourceNotFoundException(id);
        }
    }

    @Override
    public void persistAll(List<Restaurant> restaurants) throws Exception {
        try {
            restaurants.forEach(restaurant -> {
                try {
                    save(restaurant);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Restaurant> findAll() {
        List<Restaurant> allRestaurants = new ArrayList<>();
        try {
            raf.seek(0);
            raf.skipBytes(Integer.BYTES);
            while (true) {
                try {
                    raf.readShort();
                    Restaurant read = readRestaurantFromStream(raf);
                    allRestaurants.add(read);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allRestaurants;
    }

    private Restaurant readRestaurantFromStream(RandomAccessFile raf) throws IOException {
        int id = raf.readInt();
        String name = raf.readUTF();
        short categoriesLength = raf.readShort();
        String[] categories = new String[categoriesLength];
        for (int i = 0; i < categoriesLength; i++) {
            categories[i] = raf.readUTF();
        }
        String postalCode = readFixedLengthString(raf);
        String city = raf.readUTF();
        String address = raf.readUTF();
        Double latitude = raf.readDouble();
        Double longitude = raf.readDouble();
        Long timeInMillis = raf.readLong();
        short websitesLength = raf.readShort();
        String[] websites = new String[websitesLength];
        for (int i = 0; i < websitesLength; i++) {
            websites[i] = raf.readUTF();
        }
        return new Restaurant(id, name, categories, postalCode, city, address, latitude,
        longitude, Instant.ofEpochMilli(timeInMillis), websites);
    }

    private void writeRecorInStream(DataOutput raf, Restaurant restaurant) throws IOException {
        raf.writeInt(restaurant.getId());
        writeUTF(raf, restaurant.getName());
        
        String[] categories = restaurant.getCategories();
        raf.writeShort(categories.length);
        for (String category : categories) {
            writeUTF(raf, category);
        }

        writeFixedLengthString(raf, restaurant.getPostalCode());
        writeUTF(raf, restaurant.getCity());
        writeUTF(raf, restaurant.getAddress());
        raf.writeDouble(restaurant.getLatitude());
        raf.writeDouble(restaurant.getLongitude());
        raf.writeLong(restaurant.getDateUpdated().toEpochMilli());

        String[] websites = restaurant.getWebsites();
        raf.writeShort(websites.length);
        for (String website : websites) {
            writeUTF(raf, website);
        }
    }

    private short getRecordLength(Restaurant restaurant) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        writeRecorInStream(dos, restaurant);
        baos.flush();
        dos.flush();
        return (short)baos.toByteArray().length;
    }

    private void writeUTF(DataOutput raf, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        raf.writeShort(bytes.length);
        raf.write(bytes);
    }

    private String readFixedLengthString(RandomAccessFile raf) throws IOException {
        byte[] bytes = new byte[LENGTH_STRING_FIXED];
        raf.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private void writeFixedLengthString(DataOutput raf, String value) throws IOException {
        byte[] bytes = new byte[LENGTH_STRING_FIXED];
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(stringBytes, 0, bytes, 0, Math.min(stringBytes.length, LENGTH_STRING_FIXED));
        raf.write(bytes);
    }
    
    private int readLastAddedId() throws IOException {
        raf.seek(0);
        int id = raf.readInt();
        return id;
    }
    
    private void writeLastAddedId(int id) throws IOException {
        raf.seek(0);
        raf.writeInt(id);
    }


    @Override
    public void close() throws Exception {
        if (raf != null) {
            raf.close();
        }
    }
    
}
