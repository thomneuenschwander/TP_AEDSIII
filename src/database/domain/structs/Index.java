package database.domain.structs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import database.domain.persistence.Serializable;

public class Index implements Serializable, Comparable<Index> {

    private int id;
    private long address;

    public Index() {
        this(-1, -1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public Index(int id, long address) {
        this.id = id;
        this.address = address;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        var byteArray = new ByteArrayOutputStream();
        var stream = new DataOutputStream(byteArray);
        stream.writeInt(id);
        stream.writeLong(address);
        return byteArray.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] buffer) throws IOException {
        var byteArray = new ByteArrayInputStream(buffer);
        var stream = new DataInputStream(byteArray);
        id = stream.readInt();
        address = stream.readLong();
    }

    @Override
    public int getByteLength() {
        return Integer.BYTES + Long.BYTES;
    }

    @Override
    public int compareTo(Index o) {
        return Integer.compare(this.getId(), o.getId());
    }

    @Override
    public String toString() {
        return "Index [id=" + id + ", address=" + address + "]";
    }
}
