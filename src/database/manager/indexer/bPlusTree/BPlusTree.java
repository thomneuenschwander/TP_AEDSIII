package database.manager.indexer.bPlusTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import database.manager.indexer.Index;

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

    public List<Index> find(int id) throws IOException {
        List<Index> found = new ArrayList<>();
        long rootOffset = getRootOffset();
        find(id, rootOffset, found);
        return found;
    }

    public void find(int id, long pageOffset, List<Index> found) throws IOException {
        raf.seek(pageOffset);
        Page page = new Page(this.order, this.raf);
        Index[] pageIndexes = page.getIndexes();
        long[] childrens = page.getPointers();;

        int i = 0;
        for(; i < page.getCurrNumOfIndexes() && pageIndexes[i].getId() > id; i++);

        if(page.isLeaf() && pageIndexes[i].getId() == id){
            found.add(pageIndexes[i]);

        }else{
            find(id, childrens[i], found);
        }
    }

    private long getRootOffset() throws IOException {
        raf.seek(0);
        return raf.readLong();
    }

    public int getOrder() {
        return order;
    }
}
