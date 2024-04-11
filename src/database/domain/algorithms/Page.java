package database.domain.algorithms;

import java.io.IOException;

public interface Page {
    void insertKeys(int c1, int c2, long pageRef, int index);

    boolean hasAvailableSpace();

    boolean isUnderflow();

    boolean isLeaf();

    boolean isEmpty();

    void removeElement(int index);

    byte[] toByteArray() throws IOException;

    boolean hasNext();

    int getSizeBytes();

    int getOrder();

    short getCurrElements();

    long[] getChildrens();

    int[] getC1();

    int[] getC2();

    long getNext();

    void setCurrElements(short currElements);

    void setChildrens(long[] childrens);

    void setChildrens(int i, long newPointer);

    void setC1(int[] c1);

    void setC1(int i, int newC1);

    void setC2(int[] c2);

    void setC2(int i, int newC2);

    void setNext(long next);
}
