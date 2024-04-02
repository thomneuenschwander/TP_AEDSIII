package database.manager.indexer.index;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CompositeIndex extends Index {

    public static final int LENGTH_STRING_FIXED = 30;
    private String secundaryKey; 

    public CompositeIndex(int id, String secundaryKey) {
        super(id);
        if (secundaryKey.length() > LENGTH_STRING_FIXED) {
            this.secundaryKey = secundaryKey.substring(0, LENGTH_STRING_FIXED);
        } else {
            this.secundaryKey = String.format("%-" + LENGTH_STRING_FIXED + "s", secundaryKey);
        }
    }

    @Override
    public int getBytes() {
        return Integer.BYTES + LENGTH_STRING_FIXED;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(ba);
        out.writeInt(id);
        writeFixedLengthString(out, secundaryKey);
        return ba.toByteArray();
    }

    public static CompositeIndex readFromStream(DataInput in) throws IOException {
        int id = in.readInt();
        byte[] keyBytes = new byte[LENGTH_STRING_FIXED];
        in.readFully(keyBytes); 
        String secundaryKey = new String(keyBytes, "UTF-8").trim();
        return new CompositeIndex(id, secundaryKey);
    }

    private void writeFixedLengthString(DataOutput dataOutput, String value) throws IOException {
        byte[] bytes = new byte[LENGTH_STRING_FIXED];
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(stringBytes, 0, bytes, 0, Math.min(stringBytes.length, LENGTH_STRING_FIXED));
        dataOutput.write(bytes);
    }

    public String getSecundaryKey() {
        return secundaryKey;
    }

    public void setSecundaryKey(String secundaryKey) {
        this.secundaryKey = secundaryKey;
    }
}
