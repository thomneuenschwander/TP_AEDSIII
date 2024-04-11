package database.algorithms.bPlusTree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import database.domain.algorithms.Page;

public class PagePK_FK extends Page<Integer, Integer>{

    public PagePK_FK(int order) {
        super(order);
        int numOfKeys = order - 1;
        this.pk = new Integer[numOfKeys];
        this.fk = new Integer[numOfKeys];
        for(int i = 0; i < numOfKeys; i++) {
            pk[i] = -1;
            fk[i] = -1;
        }
    }

    @Override
    public byte[] toByteArray() throws IOException {
        var bao = new ByteArrayOutputStream();
        var stream = new DataOutputStream(bao);
        serialize(stream);
        return bao.toByteArray();
    }

    private void serialize(DataOutputStream stream) throws IOException {
        stream.writeShort(currElements);
        for (int i = 0; i < currElements; i++) {
            stream.writeLong(childrens[i]);
            stream.writeInt(pk[i]);
            stream.writeInt(fk[i]);
        }
        stream.writeLong(childrens[currElements]);
        byte[] emptyKeys = new byte[(Integer.BYTES * 2)];
        for (int i = currElements; i < order - 1; i++) {
            stream.write(emptyKeys);
            stream.writeLong(childrens[i + 1]);
        }
        stream.writeLong(next);
    } 

    @Override
    public void fromByteArray(byte[] buffer) throws IOException {
        var in = new ByteArrayInputStream(buffer);
        var stream = new DataInputStream(in);
        deserialize(stream);
    }

    private void deserialize(DataInputStream stream) throws IOException {
        this.currElements = stream.readShort();
        for (int i = 0; i < order - 1; i++) {
            this.childrens[i] = stream.readLong();
            this.pk[i] = stream.readInt();
            this.fk[i] = stream.readInt();
        }
        this.childrens[order - 1] = stream.readLong();
        this.next = stream.readLong();
    }

    @Override
    public void insertKeys(Integer firstKey, Integer secondKey, long pageRef, int index) {
        if(hasAvailableSpace()){
            this.pk[index] = firstKey;
            this.fk[index] = secondKey;
            this.childrens[index + 1] = pageRef;
            this.currElements++;
        }
    }

    @Override
    public void removeElement(int i) {
        this.pk[i] = 0;
        this.fk[i] = 0;
        this.childrens[i+1] = -1;
        this.currElements--;
    }   

    @Override
    protected int getByteSize() {
        int childrens = Long.BYTES * order;
        int keys = (Integer.BYTES * (order - 1)) * 2;
        return Short.BYTES + childrens + keys + Long.BYTES;
    }

    @Override
    public Integer[] getFirstKeys() {
        return this.pk;
    }

    @Override
    public void setFirstKey(int i, Integer firstKey) {
        if (i < (order - 1)) 
            this.pk[i] = firstKey;
    }

    @Override
    public Integer[] getSecondKeys() {
        return this.fk;
    }

    @Override
    public void setSecondKey(int i, Integer secondKey) {
        if (i < (order - 1)) 
            this.fk[i] = secondKey;
    } 
}
