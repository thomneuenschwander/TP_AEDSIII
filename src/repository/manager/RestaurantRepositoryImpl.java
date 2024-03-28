package repository.manager;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import domain.Restaurant;
import domain.exceptions.DuplicateIdException;
import domain.exceptions.ResourceNotFoundException;
import repository.RestaurantRepository;

/*
    Comments:

    - Negative ID represents logical Deletion. (gravestone)
    - Date persistence: milliseconds since the Unix epoch. [January 1, 1970, 00:00:00 GMT]
*/

public class RestaurantRepositoryImpl implements RestaurantRepository, AutoCloseable {

    private final File file;
    private final RandomAccessFile raf;
    private final RestaurantPersister persister;

    public RestaurantRepositoryImpl(String binaryFilePath, RestaurantPersister persister)
            throws IOException {
        this.persister = persister;
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
            short recordSize = persister.getRecordLength(restaurant);
            persistRecordWithSize(restaurant, recordSize, file.length());
            writeLastAddedId(restaurant.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void persistRecordWithSize(Restaurant restaurant, short size, long address) throws IOException {
        raf.seek(address);
        raf.writeShort(size);
        persister.writeRecorInStream(raf, restaurant);
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
                    Restaurant found = persister.readRestaurantFromStream(raf);
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

                    short updatedRecordSize = persister.getRecordLength(updatedRestaurant);
                    if (recordSize <= updatedRecordSize) {
                        persistRecordWithSize(updatedRestaurant, updatedRecordSize, recordStartPosition);
                        raf.seek(recordStartPosition + Short.BYTES);
                        persister.writeRecorInStream(raf, updatedRestaurant);
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
                    Restaurant read = persister.readRestaurantFromStream(raf);
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
