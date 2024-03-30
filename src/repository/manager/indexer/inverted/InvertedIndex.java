package repository.manager.indexer.inverted;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvertedIndex {
    private final File file;
    private final RandomAccessFile raf;

    public InvertedIndex(String binaryFile, List<String> keys) throws IOException {
        this.file = new File(binaryFile);
        if (!this.file.exists()) {
            this.file.createNewFile();
        }
        this.raf = new RandomAccessFile(file, "rw");
        buildIndexes(keys);
    }

    private void buildIndexes(List<String> keys) throws IOException {
        raf.seek(0);
        raf.writeShort(keys.size());
        Collections.sort(keys);
        for (String key : keys) {
            raf.writeUTF(key);
            raf.writeLong(-1);
        }
    }

    private long findEntry(String key) throws IOException {
        raf.seek(0);
        short indexCount = raf.readShort();
        for (int i = 0; i < indexCount; i++) {
            String currKey = raf.readUTF();
            if (currKey.equals(key)) {
                long head = raf.readLong();
                return head;
            } else {
                raf.skipBytes(Long.BYTES);
            }
        }
        return -1;
    }

    public List<Integer> find(String key) throws IOException {
        List<Integer> found = new ArrayList<>();
        long head = findEntry(key);
        if(head != -1){
            findReferences(head, found);
        }
        return found; 
    }

    private void findReferences(long head, List<Integer> found) throws IOException {
        raf.seek(head);
        int id = raf.readInt();
        long next = raf.readLong();
        found.add(id);
        if (next != -1) {
            findReferences(next, found);
        }
    }

    public void insert(String key, int id) throws IOException {
        long pos = findEntry(key);
        if(pos == -1) {
            writeNode(id, raf.getFilePointer());
        }else{
            long prevPointer = pos;
            while(pos != -1) {
                raf.seek(pos);
                raf.skipBytes(Integer.BYTES);
                pos = raf.readLong();
                prevPointer = pos;
            }
            long ref = insertEnd(id);
            raf.seek(prevPointer);
            raf.writeLong(ref);
        }
    }

    private long insertEnd(int id) throws IOException {
        long EOF = file.length();
        writeNode(id, EOF);
        return EOF;
    }

    private void writeNode(int id, long position) throws IOException {
        raf.seek(position);
        raf.writeInt(id);
        raf.writeLong(-1);
    }
}
