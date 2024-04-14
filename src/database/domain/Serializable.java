package database.domain;

import java.io.IOException;

public interface Serializable {
    byte[] toByteArray() throws IOException;

    void fromByteArray(byte[] buffer) throws IOException;

    int getByteLength();
}
