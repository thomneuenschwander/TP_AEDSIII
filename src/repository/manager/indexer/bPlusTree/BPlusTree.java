package repository.manager.indexer.bPlusTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class BPlusTree {
    private RandomAccessFile raf;
    private short order;

    public BPlusTree(String fileName, short order) throws IOException {
        this.raf = new RandomAccessFile(fileName, "rw");
        this.order = order;

        if(raf.length() == 0) {
            raf.writeLong(-1);
        }
    }

    public List<Integer> find(int id) throws IOException {
        List<Integer> found = new ArrayList<>();
        long rootOffset = getRootOffset();
        return null;
    }

    public List<Integer> find(int id, long pageOffset) throws IOException {


        raf.seek(pageOffset);
        Page page = new Page(order, raf);

        return null;
    }

    private long getRootOffset() throws IOException {
        raf.seek(0);
        return raf.readLong();
    }

    public int getOrder() {
        return order;
    }
}
