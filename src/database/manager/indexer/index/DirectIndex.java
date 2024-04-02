package database.manager.indexer.index;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class DirectIndex {
    public final int BYTES = Integer.BYTES + Long.BYTES;
    private int id;
    private long offset;

    public DirectIndex() {
        this(-1, -1);
    }

    public DirectIndex(int id){
        this(id, -1);
    }

    public DirectIndex(int id, long offset) {
        this.id = id;
        this.offset = offset;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(ba);
        out.writeInt(id);
        out.writeLong(offset);
        return ba.toByteArray();
    }

    public static DirectIndex readFromStream(DataInput in) throws IOException {
        int id = in.readInt();
        long offset = in.readLong();
        return new DirectIndex(id, offset);
    }

    public static void writeInStream(DataOutput out, DirectIndex index) throws IOException {
        out.writeInt(index.getId());
        out.writeLong(index.getOffset());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
