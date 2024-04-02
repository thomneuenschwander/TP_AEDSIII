package database.manager.indexer.index;

import java.io.IOException;

public abstract class Index {
    protected int id;

    public Index(int id){
        this.id = id;
    }

    abstract public int getBytes();

    abstract public byte[] toByteArray() throws IOException;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
