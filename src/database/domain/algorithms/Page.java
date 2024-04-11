package database.domain.algorithms;

import java.io.IOException;

public abstract class Page<T, K> {
    public final int sizeBytes;
    protected final int order;
    protected short currElements;
    protected T[] pk;
    protected K[] fk;
    protected long[] childrens;
    protected long next;

    public Page(int order) {
        this.order = order;
        this.currElements = 0;
        this.childrens = new long[order];
        for (int i = 0; i < order; i++) {
            childrens[i] = -1;
        }
        this.next = -1;
        this.sizeBytes = getByteSize();
    }

    abstract public byte[] toByteArray() throws IOException;
    
    abstract public void fromByteArray(byte[] buffer) throws IOException;

    abstract public void insertKeys(T firstKey, K secondKey, long pageRef, int index);

    abstract public void removeElement(int index);

    abstract public T[] getFirstKeys();

    abstract public void setFirstKey(int i, T firstKey);

    abstract public K[] getSecondKeys();

    abstract public void setSecondKey(int i, K secondKey); 

    abstract protected int getByteSize();

    public boolean hasAvailableSpace() {
        return currElements < (order - 1);
    }

    public boolean isLeaf() {
        return childrens[0] == -1;
    }

    public boolean isEmpty() {
        return currElements == 0;
    }

    public boolean hasNext(){
        return next != -1;
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

    public void setNext(long next) {
        this.next = next;
    }
}
