package database.domain.persistence;

public interface Record extends Serializable, Comparable<Object>, Cloneable {
    int getId();

    void setId(int id);
}
