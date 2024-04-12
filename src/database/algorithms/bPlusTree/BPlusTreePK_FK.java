package database.algorithms.bPlusTree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BPlusTreePK_FK {

    private final int order;
    private final RandomAccessFile raf;
    private int pkAux;
    private int fkAux;
    private long pageOffsetAux;
    private boolean hasGrown;
    private boolean hasShrink;

    public BPlusTreePK_FK(int order, String fileName) throws IOException {
        this.order = order;
        final String dir = "./src/database/data/bPlusTree/";
        this.raf = new RandomAccessFile(dir + fileName, "rw");
        if (raf.length() < Long.BYTES) {
            writeRootOffset(-1);
        }
    }

    public boolean save(int c1, int c2) throws IOException {
        long rootOffset = readRootOffset();
        pkAux = c1;
        fkAux = c2;
        pageOffsetAux = -1;
        hasGrown = false;

        boolean saved = save(rootOffset);
        if (hasGrown) {
            PagePK_FK newPage = new PagePK_FK(order);
            newPage.setCurrElements((short) 1);
            newPage.setFirstKey(0, pkAux);
            newPage.setSecondKey(0, fkAux);
            newPage.setChildrens(0, rootOffset);
            newPage.setChildrens(1, pageOffsetAux);
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
            pageOffsetAux = -1;
            return false;
        }
        PagePK_FK currPage = readPageFromStream(pageOffset);

        int index = lookForChildrenIndex(pkAux, fkAux, currPage);

        if (index < currPage.getCurrElements() && hasFoundKeysInPage(index, pkAux, fkAux, currPage)) {
            hasGrown = false;
            return false;
        }
        boolean saved;
        if (index == currPage.getCurrElements() || pkAux < currPage.getFirstKeys()[index]
                || (pkAux == currPage.getFirstKeys()[index] && fkAux < currPage.getSecondKeys()[index])) {
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
            currPage.insertKeys(pkAux, fkAux, pageOffsetAux, index);
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
            currPage.insertKeys(pkAux, fkAux, pageOffsetAux, index);
            if (currPage.isLeaf()) {
                pkAux = newPage.getFirstKeys()[0];
                fkAux = newPage.getSecondKeys()[0];
            } else {
                int currElements = currPage.getCurrElements();
                int biggestElementIndex = currElements - 1;

                pkAux = currPage.getFirstKeys()[biggestElementIndex];
                fkAux = currPage.getSecondKeys()[biggestElementIndex];
                currPage.setFirstKey(biggestElementIndex, 0);
                currPage.setSecondKey(biggestElementIndex, 0);

                currPage.setChildrens(currElements, -1);
                currPage.setCurrElements((short) --currElements);
            }
        } else {
            int j;
            for (j = (order - 1) - mid; j > 0 && (pkAux < newPage.getFirstKeys()[j - 1]
                    || (pkAux == newPage.getFirstKeys()[j - 1] && fkAux < newPage.getSecondKeys()[j - 1])); j--) {
                int copyC1 = currPage.getFirstKeys()[j - 1];
                int copyC2 = currPage.getSecondKeys()[j - 1];
                long copyPointer = currPage.getChildrens()[j];
                currPage.setFirstKey(j, copyC1);
                currPage.setSecondKey(j, copyC2);
                currPage.setChildrens((j + 1), copyPointer);
            }
            newPage.insertKeys(pkAux, fkAux, pageOffsetAux, j);

            pkAux = newPage.getFirstKeys()[0];
            fkAux = newPage.getSecondKeys()[0];

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
        pageOffsetAux = eof;
        raf.seek(eof);
        raf.write(newPage.toByteArray());

        raf.seek(pageOffset);
        raf.write(currPage.toByteArray());

        return true;
    }

    private int lookForChildrenIndex(int c1, int c2, PagePK_FK pg) {
        int index = 0;
        for (; index < pg.getCurrElements()
                && (c1 > pg.getFirstKeys()[index]
                        || (c1 == pg.getFirstKeys()[index] && c2 > pg.getSecondKeys()[index])); index++)
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

    public boolean delete(int pk, int fk) throws IOException {
        long rootOffset = readRootOffset();
        hasShrink = false;

        boolean deleted = delete(pk, fk, rootOffset);

        if (deleted && hasShrink) {

            PagePK_FK root = readPageFromStream(rootOffset);

            if (root.getCurrElements() == 0) {
                writeRootOffset(root.getChildrens()[0]);
            }
        }
        return deleted;
    }

    private boolean delete(int pk, int fk, long pageOffset) throws IOException {
        boolean deleted = false;
        int shrinkIndex;

        if (isPageNull(pageOffset)) {
            hasShrink = false;
            return false;
        }
        PagePK_FK currPage = readPageFromStream(pageOffset);
        int index = lookForChildrenIndex(pk, fk, currPage);

        if (index < currPage.getCurrElements() && hasFoundKeysInPage(index, pkAux, fkAux, currPage)) {
            int j = index;
            for (; j < currPage.getCurrElements() - 1; j++) {
                int copyPK = currPage.getFirstKeys()[j + 1];
                int copyFK = currPage.getSecondKeys()[j + 1];

                currPage.setFirstKey(j, copyPK);
                currPage.setSecondKey(j, copyFK);
            }
            short currElements = (short) (currPage.getCurrElements() - 1);
            currPage.setCurrElements(currElements);
            currPage.setFirstKey(currElements, 0);
            currPage.setSecondKey(currElements, 0);

            raf.seek(pageOffset);
            raf.write(currPage.toByteArray());

            hasShrink = currElements < (order - 1) / 2;
            return true;
        }
        
        if (index == currPage.getCurrElements() || pkAux < currPage.getFirstKeys()[index]
                || (pkAux == currPage.getFirstKeys()[index] && fkAux < currPage.getSecondKeys()[index])) {
            deleted = delete(pk, fk, currPage.getChildrens()[index]);
            shrinkIndex = index;
        } else {
            deleted = delete(pk, fk, currPage.getChildrens()[index + 1]);
            shrinkIndex = index + 1;
        }

        if (hasShrink) {
            long sonPageOffset = currPage.getChildrens()[shrinkIndex];
            PagePK_FK sonPage = readPageFromStream(sonPageOffset);

            long brotherPageOffset;
            PagePK_FK brotherPage;

            if (shrinkIndex > 0) {
                brotherPageOffset = currPage.getChildrens()[shrinkIndex - 1];
                brotherPage = readPageFromStream(brotherPageOffset);

                if (brotherPage.getCurrElements() > (order - 1) / 2) {
                    for (int j = sonPage.getCurrElements(); j > 0; j--) {
                        int copyPK = sonPage.getFirstKeys()[j - 1];
                        int copyFK = sonPage.getSecondKeys()[j - 1];
                        long copyChildren = sonPage.getChildrens()[j];

                        sonPage.setFirstKey(j, copyPK);
                        sonPage.setSecondKey(j, copyFK);
                        sonPage.setChildrens((j + 1), copyChildren);
                    }
                    long copyChildren = sonPage.getChildrens()[0];
                    sonPage.setChildrens(1, copyChildren);
                    sonPage.increaseCurrElements();

                    if (sonPage.isLeaf()) {
                        sonPage.setFirstKey(0, brotherPage.getFirstKeys()[brotherPage.getCurrElements() - 1]);
                        sonPage.setSecondKey(0, brotherPage.getSecondKeys()[brotherPage.getCurrElements() - 1]);
                    } else {
                        sonPage.setFirstKey(0, brotherPage.getFirstKeys()[shrinkIndex - 1]);
                        sonPage.setSecondKey(0, brotherPage.getSecondKeys()[shrinkIndex - 1]);
                    }

                    currPage.setFirstKey((shrinkIndex - 1),
                            brotherPage.getFirstKeys()[brotherPage.getCurrElements() - 1]);
                    currPage.setSecondKey((shrinkIndex - 1),
                            brotherPage.getSecondKeys()[brotherPage.getCurrElements() - 1]);

                    sonPage.setChildrens(0, brotherPage.getChildrens()[brotherPage.getCurrElements()]);
                    brotherPage.decreaseCurrElements();
                    hasShrink = false;
                } else {
                    if (!sonPage.isLeaf()) {
                        short k = brotherPage.getCurrElements();
                        brotherPage.setFirstKey(k, currPage.getFirstKeys()[shrinkIndex - 1]);
                        brotherPage.setSecondKey(k, currPage.getSecondKeys()[shrinkIndex - 1]);
                        brotherPage.setChildrens(k + 1, sonPage.getChildrens()[0]);
                        brotherPage.increaseCurrElements();
                    }
                    for (int j = 0; j < sonPage.getCurrElements(); j++) {
                        int copyPK = sonPage.getFirstKeys()[j];
                        int copyFK = sonPage.getSecondKeys()[j];
                        long copyChildren = sonPage.getChildrens()[j + 1];

                        short k = brotherPage.getCurrElements();

                        brotherPage.setFirstKey(k, copyPK);
                        brotherPage.setSecondKey(k, copyFK);
                        brotherPage.setChildrens(k + 1, copyChildren);

                        brotherPage.increaseCurrElements();
                    }
                    sonPage.setCurrElements((short) 0);

                    if (brotherPage.isLeaf())
                        brotherPage.setNext(sonPage.getNext());

                    int j = shrinkIndex - 1;
                    for (; j < currPage.getCurrElements() - 1; j++) {
                        int copyPK = currPage.getFirstKeys()[j + 1];
                        int copyFK = currPage.getSecondKeys()[j + 1];
                        long copyChildren = currPage.getChildrens()[j + 2];

                        currPage.setFirstKey(j, copyPK);
                        currPage.setSecondKey(j, copyFK);
                        currPage.setChildrens(j + 1, copyChildren);
                    }
                    currPage.setFirstKey(j, 0);
                    currPage.setSecondKey(j, 0);
                    currPage.setChildrens(j + 1, -1);
                    currPage.decreaseCurrElements();
                    hasShrink = currPage.getCurrElements() < (order - 1) / 2;
                }
            } else {
                brotherPageOffset = currPage.getChildrens()[shrinkIndex + 1];
                brotherPage = readPageFromStream(brotherPageOffset);

                if (brotherPage.getCurrElements() > (order - 1) / 2) {
                    if (sonPage.isLeaf()) {
                        short k = sonPage.getCurrElements();
                        sonPage.setFirstKey(k, brotherPage.getFirstKeys()[0]);
                        sonPage.setSecondKey(k, brotherPage.getSecondKeys()[0]);
                        sonPage.setChildrens(k + 1, brotherPage.getChildrens()[0]);
                        sonPage.increaseCurrElements();

                        currPage.setFirstKey(shrinkIndex, brotherPage.getFirstKeys()[1]);
                        currPage.setSecondKey(shrinkIndex, brotherPage.getSecondKeys()[1]);
                    } else {
                        short k = sonPage.getCurrElements();
                        sonPage.setFirstKey(k, currPage.getFirstKeys()[shrinkIndex]);
                        sonPage.setSecondKey(k, currPage.getSecondKeys()[shrinkIndex]);
                        sonPage.setChildrens(k + 1, brotherPage.getChildrens()[0]);
                        sonPage.increaseCurrElements();

                        currPage.setFirstKey(shrinkIndex, brotherPage.getFirstKeys()[0]);
                        currPage.setSecondKey(shrinkIndex, brotherPage.getSecondKeys()[0]);
                    }
                    int j = 0;
                    for (; j < brotherPage.getCurrElements() - 1; j++) {
                        int copyPK = brotherPage.getFirstKeys()[j + 1];
                        int copyFK = brotherPage.getSecondKeys()[j + 1];
                        long copyChildren = brotherPage.getChildrens()[j + 1];

                        brotherPage.setFirstKey(j, copyPK);
                        brotherPage.setSecondKey(j, copyFK);
                        brotherPage.setChildrens(j, copyChildren);
                    }
                    brotherPage.setChildrens(j, brotherPage.getChildrens()[j + 1]);
                    brotherPage.decreaseCurrElements();
                    hasShrink = false;
                } else {
                    if (!sonPage.isLeaf()) {
                        sonPage.setFirstKey(sonPage.getCurrElements(), currPage.getFirstKeys()[shrinkIndex]);
                        sonPage.setSecondKey(sonPage.getCurrElements(), currPage.getSecondKeys()[shrinkIndex]);
                        sonPage.setChildrens(sonPage.getCurrElements() + 1, brotherPage.getChildrens()[0]);
                    }
                    for (int j = 0; j < brotherPage.getCurrElements(); j++) {
                        int copyPK = brotherPage.getFirstKeys()[j];
                        int copyFK = brotherPage.getSecondKeys()[j];
                        long copyChildren = brotherPage.getChildrens()[j + 1];

                        short k = sonPage.getCurrElements();

                        sonPage.setFirstKey(k, copyPK);
                        sonPage.setSecondKey(k, copyFK);
                        sonPage.setChildrens(k + 1, copyChildren);

                        sonPage.increaseCurrElements();
                    }
                    brotherPage.setCurrElements((short) 0);
                    sonPage.setNext(brotherPage.getNext());

                    for (int j = shrinkIndex; j < currPage.getCurrElements() - 1; j++) {
                        int copyPK = currPage.getFirstKeys()[j + 1];
                        int copyFK = currPage.getSecondKeys()[j + 1];
                        long copyChildren = currPage.getChildrens()[j + 2];

                        currPage.setFirstKey(j, copyPK);
                        currPage.setSecondKey(j, copyFK);
                        currPage.setChildrens(j + 1, copyChildren);
                    }
                    currPage.decreaseCurrElements();
                    hasShrink = currPage.getCurrElements() < (order - 1) / 2;
                }
            }
            raf.seek(pageOffset);
            raf.write(currPage.toByteArray());

            raf.seek(sonPageOffset);
            raf.write(sonPage.toByteArray());

            raf.seek(brotherPageOffset);
            raf.write(brotherPage.toByteArray());
        }
        return deleted;
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
