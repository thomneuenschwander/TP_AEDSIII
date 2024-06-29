package database.algorithms;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import database.domain.algorithms.Index;
import database.domain.persistence.Record;

public class SequentialAcess<T extends Record> {
    private final RandomAccessFile raf;
    private Constructor<T> constructor;
    public static final String dataDir = "src/database/data/";
    private final int headerEnd;

    public SequentialAcess(String dataFileName, Constructor<T> constructor) throws IOException {
        this.raf = new RandomAccessFile(dataDir + dataFileName, "rw");
        this.constructor = constructor;
        this.headerEnd = Integer.BYTES;
        if (raf.length() < headerEnd)
            writeIdHeader(0);
    }

    public Optional<T> find(int id) throws Exception {
        raf.seek(headerEnd);
        while (true) {
            try {
                long currOffset = raf.getFilePointer();

                boolean grave = raf.readBoolean();
                short currRecordSize = raf.readShort();
                int currentId = raf.readInt();

                if (currentId == id && !grave) {
                    long recordOffset = currOffset + Byte.BYTES + Short.BYTES;
                    raf.seek(recordOffset);
                    T entity = deserialize(currRecordSize);
                    return Optional.of(entity);
                } else {
                    long nextOffset = currOffset + Byte.BYTES + Short.BYTES + currRecordSize;
                    raf.seek(nextOffset);
                }
            } catch (EOFException e) {
                break;
            }
        }
        return null;
    }

    public Optional<T> find(long recordOffset) throws Exception {
        raf.seek(recordOffset);
        boolean grave = raf.readBoolean();
        short size = raf.readShort();
        if(!grave)
            return Optional.of(deserialize(size));
        return null;
    }

    private T deserialize(short byteLength) throws Exception {
        T entity = constructor.newInstance();
        byte[] record = new byte[byteLength];
        raf.read(record);
        entity.fromByteArray(record);
        return entity;
    }

    public Index save(T record) throws IOException {
        setEntityId(record);
        byte[] serialized = record.toByteArray();
        long recordOffset = saveEOF(serialized);
        return new Index(record.getId(), recordOffset);
    }

    private long saveEOF(byte[] serialized) throws IOException {
        long recordOffset = raf.length();
        raf.seek(recordOffset);
        raf.writeBoolean(false);
        raf.writeShort(serialized.length);
        raf.write(serialized);
        return recordOffset;
    }

    private void setEntityId(T record) throws IOException {
        int lastIdAdded = readIdHeader();
        int updatedId = lastIdAdded + 1;
        record.setId(updatedId);
        writeIdHeader(updatedId);
    }

    public void update(int id, T updated) throws IOException {
        updated.setId(id);
        raf.seek(headerEnd);
        while (true) {
            try {
                long currOffset = raf.getFilePointer();

                boolean grave = raf.readBoolean();
                short currRecordSize = raf.readShort();
                int currentId = raf.readInt();

                if (currentId == id && !grave) {
                    long recordOffset = currOffset + Byte.BYTES + Short.BYTES;
                    raf.seek(recordOffset);

                    byte[] serialized = updated.toByteArray();

                    if (serialized.length <= currRecordSize)
                        raf.write(serialized);
                    else {
                        raf.skipBytes(Short.BYTES);
                        raf.writeBoolean(true);
                        saveEOF(serialized);
                    }
                    break;
                } else {
                    long nextOffset = currOffset + currRecordSize + Byte.BYTES + Short.BYTES;
                    raf.seek(nextOffset);
                }
            } catch (EOFException e) {
                break;
            }
        }
    }

    public void delete(int id) throws IOException {
        raf.seek(headerEnd);
        while (true) {
            try {
                long currOffset = raf.getFilePointer();

                boolean grave = raf.readBoolean();
                short currRecordSize = raf.readShort();
                int currentId = raf.readInt();

                if (currentId == id && !grave) {
                    long recordOffset = currOffset + Byte.BYTES + Short.BYTES;
                    raf.seek(recordOffset);
                    raf.writeBoolean(true);
                    break;
                } else {
                    long nextOffset = currOffset + currRecordSize + Byte.BYTES + Short.BYTES;
                    raf.seek(nextOffset);
                }
            } catch (EOFException e) {
                break;
            }
        }
    }

    public List<T> findAll() throws Exception{
        List<T> found = new ArrayList<>();
        raf.seek(headerEnd);
        while (true) {
            try {
                boolean grave = raf.readBoolean();
                short currRecordSize = raf.readShort();

                if (!grave) {
                    T record = deserialize(currRecordSize);
                    found.add(record);
                }   
            } catch (EOFException e) {
                break;
            }
        }
        return found;
    }

    private int readIdHeader() throws IOException {
        raf.seek(0);
        int id = raf.readInt();
        return id;
    }

    private void writeIdHeader(int id) throws IOException {
        raf.seek(0);
        raf.writeInt(id);
    }
}
