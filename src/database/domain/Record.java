package database.domain;

import java.io.IOException;

public interface Record extends Comparable<Object>, Cloneable {
    int getId();

    void setId(int id);

    byte[] toByteArray() throws IOException;

    void fromByteArray(byte[] stream) throws IOException;
}
