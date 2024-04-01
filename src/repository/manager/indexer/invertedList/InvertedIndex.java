package repository.manager.indexer.invertedList;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InvertedIndex {
    private RandomAccessFile indexRAF;
    private RandomAccessFile dataRAF;

    public InvertedIndex(String indexFileName, String dataRAFName) throws IOException {
        final String currentDir = "src/repository/manager/indexer/invertedList/files/";
        this.indexRAF = new RandomAccessFile(currentDir + indexFileName, "rw");
        this.dataRAF = new RandomAccessFile(currentDir + dataRAFName, "rw");
    }

    public void initializeIndexes(Collection<String> terms) throws IOException {
        for (String term : terms) {
            newKey(term.toUpperCase());
        }
    }

    private void newKey(String key) throws IOException {
        indexRAF.seek(indexRAF.length());
        indexRAF.writeUTF(key.toUpperCase());
        indexRAF.writeLong(-1);
    }

    private long newNode(int id) throws IOException {
        long newNodePos = dataRAF.length();
        dataRAF.seek(newNodePos);
        dataRAF.writeInt(id);
        dataRAF.writeLong(-1);
        return newNodePos;
    }

    private long findKeyOffset(String key) throws IOException {
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

    public void insert(String key, int id) throws IOException {
        long keyOffset = findKeyOffset(key);
        if (keyOffset != -1) {
            long head = getNodeHead(keyOffset);
            if (head == -1) {
                long nodePos = newNode(id);
                indexRAF.seek(keyOffset);
                indexRAF.readUTF();
                indexRAF.writeLong(nodePos);
            } else {
                dataRAF.seek(head);
                dataRAF.skipBytes(Integer.BYTES);

                long prevOffset = dataRAF.getFilePointer();
                long next = dataRAF.readLong();

                while (next != -1) {
                    dataRAF.skipBytes(Integer.BYTES);
                    prevOffset = dataRAF.getFilePointer();
                    next = dataRAF.readLong();
                }
                long nodePos = newNode(id);
                dataRAF.seek(prevOffset);
                dataRAF.writeLong(nodePos);
            }
        }
    }

    private long getNodeHead(long keyOffset) throws IOException {
        indexRAF.seek(keyOffset);
        indexRAF.readUTF();
        long nodeHead = indexRAF.readLong();
        return nodeHead;
    }

    public List<Integer> find(String key) throws IOException {
        List<Integer> found = new ArrayList<>();
        long keyOffset = findKeyOffset(key);
        long head = getNodeHead(keyOffset);
        if (head != -1) {
            findInLinkedList(head, found);
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
    }
}
