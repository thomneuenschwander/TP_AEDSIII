package repository.manager.indexer.invertedList;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InvertedIndexImpl {
    private final File indexFile;
    private final File dataFile;
    private RandomAccessFile indexRAF;
    private RandomAccessFile dataRAF;

    public InvertedIndexImpl(String indexFileName, String dataRAFName, Collection<String> terms) throws IOException {
        final String currentDir = "src/repository/manager/indexer/invertedList/files/";
        this.indexFile = new File(currentDir+indexFileName);
        this.dataFile = new File(currentDir+dataRAFName);
        this.indexRAF = new RandomAccessFile(this.indexFile, "rw");
        this.dataRAF = new RandomAccessFile(this.dataFile, "rw");
        initializeIndexes(terms);
    }

    private void initializeIndexes(Collection<String> terms) throws IOException {
        indexRAF.seek(0);
        for (String key : terms) {
            indexRAF.writeUTF(key);
            indexRAF.writeLong(-1);
        }
    }

    public void insert(String key, int id) throws IOException {
        long keyMapPos = findKeyMap(key);
        if (keyMapPos == -1) {
            return;
        }
        long linkedHeadPos = findIndexHead(key);
        if (linkedHeadPos == -1) {
            long newIndexPos = dataRAF.length();
            dataRAF.seek(newIndexPos);
            dataRAF.writeInt(id);
            dataRAF.writeLong(-1);
            indexRAF.seek(keyMapPos);
            indexRAF.readUTF();
            indexRAF.writeLong(newIndexPos);
        } else {
            dataRAF.seek(linkedHeadPos);
            dataRAF.skipBytes(Integer.BYTES);

            long prevOffset = dataRAF.getFilePointer();
            long next = dataRAF.readLong();
            
            while (next != -1) {
                dataRAF.skipBytes(Integer.BYTES);
                prevOffset = dataRAF.getFilePointer();
                next = dataRAF.readLong();
            }
            long newIndexPos = dataRAF.length();
            dataRAF.seek(newIndexPos);
            dataRAF.writeInt(id);
            dataRAF.writeLong(-1);
            dataRAF.seek(prevOffset);
            dataRAF.writeLong(newIndexPos);
        }

    }

    private long findIndexHead(String key) throws IOException {
        indexRAF.seek(0);
        while (indexRAF.getFilePointer() < indexRAF.length()) {
            String currentKey = indexRAF.readUTF();
            long dataIndex = indexRAF.readLong();
            if (currentKey.equals(key)) {
                return dataIndex;
            }
        }
        return -1;
    }

    private long findKeyMap(String key) throws IOException {
        indexRAF.seek(0);
        while (indexRAF.getFilePointer() < indexRAF.length()) {
            long pos = indexRAF.getFilePointer();
            String currentKey = indexRAF.readUTF();
            indexRAF.skipBytes(Long.BYTES);
            if (currentKey.equals(key)) {
                return pos;
            }
        }
        return -1;
    }

    public List<Integer> find(String key) throws IOException {
        List<Integer> found = new ArrayList<>();
        long linkedHeadPos = findIndexHead(key);
        if (linkedHeadPos != -1) {
            findInLinkedList(linkedHeadPos, found);
            return found;
        }
        return found;
    }

    private void findInLinkedList(long offset, List<Integer> found) throws IOException {
        if (offset != -1) {
            dataRAF.seek(offset);
            int id = dataRAF.readInt();
            long nextNode = dataRAF.readLong();
            found.add(id);
            findInLinkedList(nextNode, found);
        }
    }

    public void close() throws IOException {
        if (indexRAF != null) {
            indexRAF.close();
        }
        if (dataRAF != null) {
            dataRAF.close();
        }
        // indexFile.delete();
        // dataFile.delete();
    }
}
