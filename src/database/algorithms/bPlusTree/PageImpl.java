package database.algorithms.bPlusTree;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

import database.domain.algorithms.Page;

public class PageImpl implements Page {
    private final int sizeBytes;
    private final int order;
    private short currElements;
    private long[] childrens;
    private int[] c1;
    private int[] c2;
    private long next;

    public PageImpl(int order, DataInput in) throws IOException {
        this.childrens = new long[order];
        this.c1 = new int[order-1];
        this.c2 = new int[order-1];
        this.order = order;
        this.currElements = in.readShort();
        for (int i = 0; i < order - 1; i++) {
            this.childrens[i] = in.readLong();
            this.c1[i] = in.readInt();
            this.c2[i] = in.readInt();
        }
        this.childrens[order - 1] = in.readLong();
        this.next = in.readLong();
        this.sizeBytes = getByteSize();
    }

    public PageImpl(int order) {
        this.order = order;
        this.currElements = 0;
        this.childrens = new long[order];
        this.c1 = new int[order - 1];
        this.c2 = new int[order - 1];
        for (int i = 0; i < order - 1; i++) {
            c1[i] = -1;
            c2[i] = -1;
            childrens[i] = -1;
        }
        this.childrens[order-2] = -1;
        this.next = -1;
        this.sizeBytes = getByteSize();
    }

    public void insertKeys(int c1, int c2, long pageRef, int index) {
        if(hasAvailableSpace()){
            this.c1[index] = c1;
            this.c2[index] = c2;
            this.childrens[index + 1] = pageRef;
            this.currElements++;
        }
    }

    public boolean hasAvailableSpace() {
        return currElements < (order - 1);
    }

    public boolean isUnderflow() {
        return true;
    }

    public boolean isLeaf() {
        return childrens[0] == -1;
    }

    public boolean isEmpty() {
        return currElements == 0;
    }

    public void removeElement(int index) {
        this.c1[index] = 0;
        this.c2[index] = 0;
        this.childrens[index+1] = -1;
        this.currElements--;
    }

    private int getByteSize() {
        int childrens = Long.BYTES * order;
        int keys = (Integer.BYTES * (order - 1)) * 2;
        return Short.BYTES + childrens + keys;
    }

    public byte[] toByteArray() throws IOException {
        var bao = new ByteArrayOutputStream();
        var out = new DataOutputStream(bao);
        out.writeShort(currElements);
        for (int i = 0; i < currElements; i++) {
            out.writeLong(childrens[i]);
            out.writeInt(c1[i]);
            out.writeInt(c2[i]);
        }
        out.writeLong(childrens[currElements]);
        var emptyKeys = new byte[(Integer.BYTES * 2)];
        for (int i = currElements; i < order - 1; i++) {
            out.write(emptyKeys);
            out.writeLong(childrens[i + 1]);
        }
        out.writeLong(next);
        return bao.toByteArray();
    }

    public boolean hasNext(){
        return next != -1;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public int getOrder() {
        return order;
    }

    public short getCurrElements() {
        return currElements;
    }

    public long[] getChildrens() {
        return childrens;
    }

    public int[] getC1() {
        return c1;
    }

    public int[] getC2() {
        return c2;
    }

    public long getNext() {
        return next;
    }

    public void setCurrElements(short currElements) {
        this.currElements = currElements;
    }

    public void setChildrens(long[] childrens) {
        this.childrens = childrens;
    }

    public void setChildrens(int i, long newPointer) {
        if (i < order) 
            this.childrens[i] = newPointer;
    }

    public void setC1(int[] c1) {
        this.c1 = c1;
    }

    public void setC1(int i, int newC1) {
        if (i < (order - 1)) 
            this.c1[i] = newC1;
    }

    public void setC2(int[] c2) {
        this.c2 = c2;
    }

    public void setC2(int i, int newC2) {
        if (i < (order - 1)) 
            this.c2[i] = newC2;
    }

    public void setNext(long next) {
        this.next = next;
    }
}
