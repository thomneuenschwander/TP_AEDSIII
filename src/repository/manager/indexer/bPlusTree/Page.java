package repository.manager.indexer.bPlusTree;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

import repository.manager.indexer.Index;

public class Page {
    public final int BYTES;
    public final int indexBYTES = Integer.BYTES + Long.BYTES;
    private short order;
    public final int maxNumOfIndexes;
    private short currNumOfIndexes;
    private Index[] indexes;
    private long[] pointers;
    private long nextNodePointer;

    public Page(short orderTree) {
        this.maxNumOfIndexes = orderTree - 1;
        this.order = orderTree;
        this.currNumOfIndexes = 0;
        this.pointers = new long[order];
        this.indexes = new Index[maxNumOfIndexes];
        this.BYTES = (Long.BYTES * order) + (indexBYTES * maxNumOfIndexes) + Short.BYTES;
        for (int i = 0; i < maxNumOfIndexes; i++) {
            this.indexes[i] = new Index();
            this.pointers[i] = -1;
        }
        this.pointers[order-1] = -1;
        this.nextNodePointer = -1;
    }

    public Page(short orderTree, DataInput in) throws IOException {
        this.order = orderTree;
        this.maxNumOfIndexes = orderTree-1;
        readPageFromStream(in);
        this.BYTES = (Long.BYTES * order) + (indexBYTES * maxNumOfIndexes) + Short.BYTES;
    }

    private void readPageFromStream(DataInput in) throws IOException {
        this.currNumOfIndexes = in.readShort();
        for(int i = 0; i < maxNumOfIndexes; i++){
            this.pointers[i] = in.readLong();
            this.indexes[i] = Index.readFromStream(in);
        }
        this.pointers[maxNumOfIndexes] = in.readLong();
        this.nextNodePointer = in.readLong();
    }

    public boolean isLeaf(){
        return pointers[0] == -1;
    }

    protected byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(ba);

        out.writeShort(currNumOfIndexes);

        for (int i = 0; i < currNumOfIndexes; i++) {
            out.writeLong(pointers[i]);
            out.write(indexes[i].toByteArray());
        }
        out.writeLong(pointers[currNumOfIndexes]);

        for (int i = currNumOfIndexes; i < maxNumOfIndexes; i++) {
            Index.writeInStream(out, new Index());
            out.writeLong(pointers[i + 1]);
        }
        out.writeLong(nextNodePointer);

        return ba.toByteArray();
    }

    public short getOrder() {
        return order;
    }

    public void setOrder(short order) {
        this.order = order;
    }

    public short getCurrNumOfIndexes() {
        return currNumOfIndexes;
    }

    public void setCurrNumOfIndexes(short currNumOfIndexes) {
        this.currNumOfIndexes = currNumOfIndexes;
    }

    public long getNextNodePointer() {
        return nextNodePointer;
    }

    public void setNextNodePointer(long nextNodePointer) {
        this.nextNodePointer = nextNodePointer;
    }

    public Index[] getIndexes() {
        return indexes;
    }

    public long[] getPointers() {
        return pointers;
    }

    public void setPointers(long[] pointers) {
        this.pointers = pointers;
    }

    public void setIndexes(Index[] indexes) {
        this.indexes = indexes;
    }

}
