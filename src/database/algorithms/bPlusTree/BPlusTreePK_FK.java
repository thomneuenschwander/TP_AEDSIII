package database.algorithms.bPlusTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BPlusTreePK_FK {

    private final int order;
    private final RandomAccessFile raf;
    private int c1Aux;
    private int c2Aux;
    private long pageAux;
    private boolean hasGrown;

    public BPlusTreePK_FK(int order, String fileName) throws IOException {
        this.order = order;
        this.raf = new RandomAccessFile(fileName, "rw");
        if (raf.length() < Long.BYTES) {
            writeRootOffset(-1);
        }
    }

    public boolean save(int c1, int c2) throws IOException {
        long rootOffset = readRootOffset();
        c1Aux = c1;
        c2Aux = c2;
        pageAux = -1;
        hasGrown = false;

        boolean saved = save(rootOffset);
        if (hasGrown) {
            PagePK_FK newPage = new PagePK_FK(order);
            newPage.setCurrElements((short) 1);
            newPage.setFirstKey(0, c1Aux);
            newPage.setSecondKey(0, c2Aux);
            newPage.setChildrens(0, rootOffset);
            newPage.setChildrens(1, pageAux);
            long newRootOffset = raf.length();
            raf.seek(newRootOffset);
            raf.write(newPage.toByteArray());
            writeRootOffset(newRootOffset);
        }

        return saved;
    }

    private boolean save(long pageOffset) throws IOException {
        if (isPageNull(pageOffset)) {
            hasGrown = true;
            pageAux = -1;
            return false;
        }
        PagePK_FK currPage = readPageFromStream(pageOffset);

        int index = lookForChildrenIndex(c1Aux, c2Aux, currPage);

        if (index < currPage.getCurrElements() && hasFoundKeysInPage(index, c1Aux, c2Aux, currPage)) {
            hasGrown = false;
            return false;
        }
        boolean saved;
        if (index == currPage.getCurrElements() || c1Aux < currPage.getFirstKeys()[index]
                || (c1Aux == currPage.getFirstKeys()[index] && c2Aux < currPage.getSecondKeys()[index])) {
            saved = save(currPage.getChildrens()[index]);
        } else {
            saved = save(currPage.getChildrens()[index + 1]);
        }
        if (!hasGrown) {
            return saved;
        }

        if (currPage.hasAvailableSpace()) {
            for (int j = currPage.getCurrElements(); j > index; j--) {
                int copyC1 = currPage.getFirstKeys()[j - 1];
                int copyC2 = currPage.getSecondKeys()[j - 1];
                long copyPointer = currPage.getChildrens()[j];
                currPage.setFirstKey(j, copyC1);
                currPage.setSecondKey(j, copyC2);
                currPage.setChildrens((j + 1), copyPointer);
            }
            currPage.insertKeys(c1Aux, c2Aux, pageAux, index);
            raf.seek(pageOffset);
            raf.write(currPage.toByteArray());
            hasGrown = false;
            return true;
        }
        PagePK_FK newPage = new PagePK_FK(order);
        splitAndCopyPage(newPage, currPage);
        int mid = (order - 1) / 2;
        if (index <= mid) {
            for (int j = mid; j > 0 && j > index; j--) {
                int copyC1 = currPage.getFirstKeys()[j - 1];
                int copyC2 = currPage.getSecondKeys()[j - 1];
                long copyPointer = currPage.getChildrens()[j];
                currPage.setFirstKey(j, copyC1);
                currPage.setSecondKey(j, copyC2);
                currPage.setChildrens((j + 1), copyPointer);
            }
            currPage.insertKeys(c1Aux, c2Aux, pageAux, index);
            if (currPage.isLeaf()) {
                c1Aux = newPage.getFirstKeys()[0];
                c2Aux = newPage.getSecondKeys()[0];
            } else {
                int currElements = currPage.getCurrElements();
                int biggestElementIndex = currElements - 1;

                c1Aux = currPage.getFirstKeys()[biggestElementIndex];
                c2Aux = currPage.getSecondKeys()[biggestElementIndex];
                currPage.setFirstKey(biggestElementIndex, 0);
                currPage.setSecondKey(biggestElementIndex, 0);

                currPage.setChildrens(currElements, -1);
                currPage.setCurrElements((short) --currElements);
            }
        } else {
            int j;
            for (j = (order - 1) - mid; j > 0 && (c1Aux < newPage.getFirstKeys()[j - 1]
                    || (c1Aux == newPage.getFirstKeys()[j - 1] && c2Aux < newPage.getSecondKeys()[j - 1])); j--) {
                int copyC1 = currPage.getFirstKeys()[j - 1];
                int copyC2 = currPage.getSecondKeys()[j - 1];
                long copyPointer = currPage.getChildrens()[j];
                currPage.setFirstKey(j, copyC1);
                currPage.setSecondKey(j, copyC2);
                currPage.setChildrens((j + 1), copyPointer);
            }
            newPage.insertKeys(c1Aux, c2Aux, pageAux, j);

            c1Aux = newPage.getFirstKeys()[0];
            c2Aux = newPage.getSecondKeys()[0];

            if (!currPage.isLeaf()) {
                j = 0;
                for (; j < newPage.getCurrElements() - 1; j++) {
                    int copyC1 = newPage.getFirstKeys()[j + 1];
                    int copyC2 = newPage.getSecondKeys()[j + 1];
                    long copyPointer = newPage.getChildrens()[j + 1];

                    newPage.setFirstKey(j, copyC1);
                    newPage.setSecondKey(j, copyC2);
                    newPage.setChildrens(j, copyPointer);
                }
                long copyPointer = newPage.getChildrens()[j + 1];
                newPage.setChildrens(j, copyPointer);

                newPage.removeElement(j);
            }
        }
        long eof = raf.length();

        if (currPage.isLeaf()) {
            newPage.setNext(currPage.getNext());
            currPage.setNext(eof);
        }
        pageAux = eof;
        raf.seek(eof);
        raf.write(newPage.toByteArray());

        raf.seek(pageOffset);
        raf.write(currPage.toByteArray());

        return true;
    }

    private int lookForChildrenIndex(int c1, int c2, PagePK_FK pg) {
        int index = 0;
        for (; index < pg.getCurrElements()
                && (c1 > pg.getFirstKeys()[index] || (c1 == pg.getFirstKeys()[index] && c2 > pg.getSecondKeys()[index])); index++)
            ;
        return index;
    }

    private int lookForChildrenIndex(int c1, PagePK_FK pg) {
        int index = 0;
        for (; index < pg.getCurrElements()
                && c1 > pg.getFirstKeys()[index]; index++)
            ;
        return index;
    }

    private boolean hasFoundKeysInPage(int index, int c1, int c2, PagePK_FK pg) {
        return (c1 == pg.getFirstKeys()[index] && c2 == pg.getSecondKeys()[index] && pg.isLeaf());
    }

    private void splitAndCopyPage(PagePK_FK newPage, PagePK_FK oldPage) {
        if (newPage.isEmpty()) {
            int mid = (order - 1) / 2;
            for (int j = 0; j < ((order - 1) - mid); j++) {
                newPage.setFirstKey(j, oldPage.getFirstKeys()[j + mid]);
                newPage.setSecondKey(j, oldPage.getSecondKeys()[j + mid]);
                newPage.setChildrens((j + 1), oldPage.getChildrens()[j + mid + 1]);
                oldPage.setFirstKey(j + mid, 0);
                oldPage.setSecondKey(j + mid, 0);
                oldPage.setChildrens(j + mid + 1, -1);
            }
            newPage.setChildrens(0, oldPage.getChildrens()[mid]);
            newPage.setCurrElements((short) (order - 1 - mid));
            oldPage.setCurrElements((short) mid);
        }
    }

    public Optional<List<Integer>> find(int c1) throws IOException {
        long rootOffset = readRootOffset();
        if (isPageNull(rootOffset)) {
            return null;
        }
        return find(c1, rootOffset);
    }

    private Optional<List<Integer>> find(int c1, long pageOffset) throws IOException {
        if (isPageNull(pageOffset)) {
            return null;
        }
        PagePK_FK currPage = readPageFromStream(pageOffset);
        int index = lookForChildrenIndex(c1, currPage);

        if (index < currPage.getCurrElements() && currPage.isLeaf() && c1 == currPage.getFirstKeys()[index]) {
            List<Integer> found = new ArrayList<>();
            // TODO refatorar essa seguinte parte
            while (c1 <= currPage.getFirstKeys()[index]) {
                if (c1 == currPage.getFirstKeys()[index]) {
                    found.add(currPage.getSecondKeys()[index]);
                }
                index++;
                if (index == currPage.getCurrElements()) {
                    if (!currPage.hasNext()) {
                        break;
                    }
                    long nextOffset = currPage.getNext();
                    currPage = readPageFromStream(nextOffset);
                    index = 0;
                }
            }
            return Optional.ofNullable(found);
        } else if (index == currPage.getCurrElements() && currPage.isLeaf()) {
            if (!currPage.hasNext()) {
                return null;
            }
            currPage = readPageFromStream(currPage.getNext());
            index = 0;
            if (c1 <= currPage.getFirstKeys()[index]) {
                List<Integer> found = new ArrayList<>();
                // TODO refatorar essa seguinte parte
                while (c1 <= currPage.getFirstKeys()[index]) {
                    if (c1 == currPage.getFirstKeys()[index]) {
                        found.add(currPage.getSecondKeys()[index]);
                    }
                    index++;
                    if (index == currPage.getCurrElements()) {
                        if (!currPage.hasNext()) {
                            break;
                        }
                        long nextOffset = currPage.getNext();
                        currPage = readPageFromStream(nextOffset);
                        index = 0;
                    }
                }
                return Optional.ofNullable(found);
            } else {
                return null;
            }
        }
        if (index == currPage.getCurrElements() || c1 <= currPage.getFirstKeys()[index]) {
            return find(c1, currPage.getChildrens()[index]);
        } else {
            return find(c1, currPage.getChildrens()[index + 1]);
        }
    }

    private PagePK_FK readPageFromStream(long pageOffset) throws IOException {
        raf.seek(pageOffset);
        PagePK_FK page = new PagePK_FK(order);
        byte[] buffer = new byte[page.sizeBytes];
        raf.read(buffer);
        page.fromByteArray(buffer);
        return page;
    }

    public boolean isEmpty() throws IOException {
        long rootOffset = readRootOffset();
        return isPageNull(rootOffset);
    }

    private boolean isPageNull(long rootOffset) {
        return rootOffset == -1;
    }

    private long readRootOffset() throws IOException {
        raf.seek(0);
        return raf.readLong();
    }

    private void writeRootOffset(long offset) throws IOException {
        raf.seek(0);
        raf.writeLong(offset);
    }

    public int getOrder() {
        return order;
    }
}
