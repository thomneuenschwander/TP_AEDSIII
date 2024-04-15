package database.algorithms.extensibleHash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import database.domain.Serializable;
import database.domain.structs.Index;

public class Bucket<T extends Index> implements Serializable {
    protected byte localDeep;
    protected short maxLimit;
    protected List<T> indexes;
    protected Constructor<T> constructor;
    protected short quantity;
    protected final int indexByteLength;

    public Bucket(short maxLimit, Constructor<T> constructor) throws Exception {
        this((byte) 0, maxLimit, constructor);
    }

    public Bucket(byte localDeep, short maxLimit, Constructor<T> constructor) throws Exception {
        this.localDeep = localDeep;
        this.maxLimit = maxLimit;
        this.quantity = 0;
        this.constructor = constructor;
        this.indexes = new ArrayList<>(maxLimit);
        this.indexByteLength = constructor.newInstance().getByteLength();
    }

    public T find(int key) {
        if (isEmpty())
            return null;
        int i = 0;
        while (i < quantity && key > indexes.get(i).getId())
            i++;
        if (i < quantity && key == indexes.get(i).getId())
            return indexes.get(i);
        else
            return null;
    }

    public boolean save(T index) {
        if (isFull())
            return false;

        int i = quantity - 1;
        while (i >= 0 && index.getId() < indexes.get(i).getId())
            i--;
        indexes.add(i + 1, index);
        quantity++;
        return true;
    }

    // public boolean save(T index){
    // if(isFull())
    // return false;

    // indexes.addLast(index);
    // quantity++;
    // return true;
    // }

    public boolean isFull() {
        return quantity >= maxLimit;
    }

    public boolean isEmpty() {
        return quantity == 0;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        var baos = new ByteArrayOutputStream();
        var out = new DataOutputStream(baos);
        out.writeByte(localDeep);
        out.writeShort(quantity);
        for (T index : indexes) {
            out.write(index.toByteArray());
        }
        var emptyIndex = new byte[indexByteLength];
        for (int i = quantity; i < maxLimit; i++) {
            out.write(emptyIndex);
        }
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] buffer) throws IOException {
        var bais = new ByteArrayInputStream(buffer);
        var in = new DataInputStream(bais);
        localDeep = in.readByte();
        quantity = in.readShort();
        indexes = new ArrayList<>(maxLimit);

        byte[] indexBuffer = new byte[indexByteLength];

        try {
            for (int i = 0; i < maxLimit; i++) {
                in.read(indexBuffer);
                T index = constructor.newInstance();
                index.fromByteArray(indexBuffer);
                indexes.add(index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getByteLength() {
        return Byte.BYTES + Short.BYTES + (indexByteLength * maxLimit);
    }

    @Override
    public String toString() {
        return "Bucket [indexes=" + indexes + ", quantity=" + quantity + "]";
    }
}
