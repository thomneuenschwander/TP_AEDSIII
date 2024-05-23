package database.algorithms.compression;

import java.util.BitSet;

/**
 * A class to represent a sequence of integers using a fixed number of bits for
 * each integer.
 */
public class IntegerBitSequence {
    private final int bitsPerNumber;
    private BitSet bitSet;
    private int currentBitIndex;

    /**
     * Constructs an IntegerBitSequence with a specified number of bits per integer.
     *
     * @param bitsPerNumber the number of bits used to represent each integer
     */
    public IntegerBitSequence(int bitsPerNumber) {
        this.bitsPerNumber = bitsPerNumber;
        this.currentBitIndex = 0;
        this.bitSet = new BitSet();
    }

    /**
     * Adds an integer to the bit sequence.
     *
     * @param n the integer to be added
     */
    public void add(int n) {
        for (int i = 0; i < bitsPerNumber; i++, currentBitIndex++) {
            setBit(currentBitIndex, (n & 1) == 1);
            n >>= 1;
        }
    }

    /**
     * Sets or clears a bit at the specified position.
     *
     * @param position the position of the bit
     * @param value    true to set the bit, false to clear the bit
     */
    private void setBit(int position, boolean value) {
        if (value) {
            this.bitSet.set(position);
        } else {
            this.bitSet.clear(position);
        }
    }

    /**
     * Retrieves the integer stored at the specified index.
     *
     * @param index the index of the integer to retrieve
     * @return the integer stored at the specified index
     */
    public int get(int index) {
        int pos = index * bitsPerNumber;
        int numberStoraged = 0;
        for (int j = 0; j < bitsPerNumber; j++) {
            if (bitSet.get(pos + j)) {
                numberStoraged |= (1 << j);
            }
        }
        return numberStoraged;
    }

    /**
     * Returns the number of integers stored in the sequence.
     *
     * @return the number of integers stored in the sequence
     */
    public int size() {
        return currentBitIndex / bitsPerNumber;
    }

    /**
     * Sets the bit sequence from a byte array.
     *
     * @param numOfByteIntegers the number of integers in the byte array
     * @param bytes             the byte array representing the bit sequence
     */
    public void setBytes(int numOfByteIntegers, byte[] bytes) {
        currentBitIndex = numOfByteIntegers * bitsPerNumber;
        this.bitSet = BitSet.valueOf(bytes);
    }

    /**
     * Returns a byte array representation of the bit sequence.
     *
     * @return a byte array representation of the bit sequence
     */
    public byte[] getBytes() {
        return bitSet.toByteArray();
    }
}
