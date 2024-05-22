package database.algorithms.compression.lzw;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import database.algorithms.compression.Compression;
import database.algorithms.compression.IntegerBitSequence;

public class LZWCompression {
    private static final int BASICS_SYMBOLS_SIZE = 256;
    private static final int DICTIONARY_MAX_SIZE = 4096;
    private static final int BITS_PER_INDEX = (int) Math.ceil(Compression.log2(DICTIONARY_MAX_SIZE));

    public static void compress(FileInputStream src, ObjectOutputStream dst) {
        try {
            byte[] fileBytes = new byte[src.available()];
            src.read(fileBytes);
            src.close();

            List<List<Byte>> dictionary = initializeDictionary();
            List<Integer> LZWCodes = generateLZWCodes(dictionary, fileBytes);
            byte[] compressed = serializeLZWCodes(LZWCodes);

            dst.writeObject(compressed);
            dst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<List<Byte>> initializeDictionary() {
        List<List<Byte>> dictionary = new ArrayList<>(DICTIONARY_MAX_SIZE);
        for (int i = 0; i < BASICS_SYMBOLS_SIZE; i++) {
            List<Byte> basicSymbol = new ArrayList<>(Byte.BYTES);
            basicSymbol.add((byte) i);
            dictionary.add(basicSymbol);
        }
        return dictionary;
    }

    private static List<Integer> generateLZWCodes(List<List<Byte>> dictionary, byte[] bytes) {
        List<Integer> output = new ArrayList<>();
        for (int i = 0; i < bytes.length; i++) {
            List<Byte> in = new ArrayList<>();
            in.add(bytes[i++]);

            int dictIndex = dictionary.indexOf(in);
            int code = dictIndex;

            while (dictIndex != -1 && i < bytes.length) {
                in.add(bytes[i++]);
                code = dictIndex;
                dictIndex = dictionary.indexOf(in);
            }

            output.add(code);

            if (dictionary.size() < DICTIONARY_MAX_SIZE)
                dictionary.add(in);
        }
        return output;
    }

    private static byte[] serializeLZWCodes(List<Integer> LZWCodes) {
        IntegerBitSequence bis = new IntegerBitSequence(BITS_PER_INDEX);
        LZWCodes.forEach(code -> {
            bis.add(code);
        });
        return bis.getBytes();
    }
}
