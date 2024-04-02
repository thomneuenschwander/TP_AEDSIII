package database.manager;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import domain.Restaurant;
import domain.exceptions.DuplicateIdException;
import domain.exceptions.ResourceNotFoundException;

public class SequentialAcess {
    private final RandomAccessFile raf;
    private final RestaurantSerializeble persister;

    public SequentialAcess(String dataFileName) throws IOException {
        final String dir = "src/database/manager/data/";
        this.raf = new RandomAccessFile(dir + dataFileName, "rw");
        this.persister = new RestaurantSerializeble(5);
        writeLastIDHeader(-1);
    }

    public long save(Restaurant restaurant) throws Exception {
        try {
            int lastID = readLastIDHeader();
            if (restaurant.getId() >= 0 && restaurant.getId() <= lastID) {
                throw new DuplicateIdException(restaurant.getId());
            } else if (restaurant.getId() < 0) {
                restaurant.setId(++lastID);
            }
            short recordSize = persister.getRecordLength(restaurant);
            long recordOffset = raf.length();
            persistRecordWithSize(restaurant, recordSize, recordOffset);
            writeLastIDHeader(restaurant.getId());
            return recordOffset;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void persistRecordWithSize(Restaurant restaurant, short size, long address) throws IOException {
        raf.seek(address);
        raf.writeShort(size);
        persister.writeRecorInStream(raf, restaurant);
    }

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
                        persistRecordWithSize(updatedRestaurant, updatedRecordSize, raf.length());
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

    private int readLastIDHeader() throws IOException {
        raf.seek(0);
        int id = raf.readInt();
        return id;
    }

    private void writeLastIDHeader(int id) throws IOException {
        raf.seek(0);
        raf.writeInt(id);
    }
}
