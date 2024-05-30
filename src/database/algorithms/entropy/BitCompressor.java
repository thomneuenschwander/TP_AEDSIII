package database.algorithms.entropy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class BitCompressor {
    public static final int MAX_VALUE = 4095;
    public static final int BITS_PER_NUMBER = 12;

    public static byte[] compressTo12Bits(List<Integer> numbers) throws IllegalArgumentException {
        BitSet bitSet = new BitSet(numbers.size() * BITS_PER_NUMBER);

        int bitIndex = 0;
        for (int number : numbers) {
            if (number < 0 || number > MAX_VALUE) 
                throw new IllegalArgumentException("Number must be between 0 and " + MAX_VALUE);

            for (int i = 0; i < BITS_PER_NUMBER; i++) {
                if ((number & (1 << i)) != 0) 
                    bitSet.set(bitIndex + i);
            }
            bitIndex += BITS_PER_NUMBER;
        }

        int bitSetLength = bitSet.length();
        int byteArraySize = (bitSetLength + 7) / 8;

        byte[] byteArray = new byte[byteArraySize + 4];
        byte[] bitsToBytes = bitSet.toByteArray();

        System.arraycopy(intToByteArray(numbers.size()), 0, byteArray, 0, 4);
        System.arraycopy(bitsToBytes, 0, byteArray, 4, bitsToBytes.length);

        return byteArray;
    }

    public static List<Integer> decompressFrom12Bits(byte[] compressedNumbers) throws IOException {
        byte[] lengthBytes = new byte[4];
        System.arraycopy(compressedNumbers, 0, lengthBytes, 0, 4);

        int numberOfNumbers = byteArrayToInt(lengthBytes);

        int bitSetLength = numberOfNumbers * BITS_PER_NUMBER;
        byte[] numbersData = new byte[(bitSetLength + 7) / 8];
        System.arraycopy(compressedNumbers, 4, numbersData, 0, numbersData.length);

        BitSet bitSet = BitSet.valueOf(numbersData);
        List<Integer> numbers = new ArrayList<>(numberOfNumbers);

        for (int i = 0; i < numberOfNumbers; i++) {
            int number = 0;
            for (int j = 0; j < BITS_PER_NUMBER; j++) {
                if (bitSet.get(i * BITS_PER_NUMBER + j)) 
                    number |= (1 << j);
            }
            numbers.add(number);
        }

        return numbers;
    }

    private static int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    private static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }
}
