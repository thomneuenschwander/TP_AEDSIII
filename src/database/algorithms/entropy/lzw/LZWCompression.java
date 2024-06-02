package database.algorithms.entropy.lzw;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import database.algorithms.entropy.BitCompressor;

public class LZWCompression {
    public static void main(String[] args) {
        try (var fis = new FileInputStream("source.db"); var fos = new FileOutputStream("destine.db")) {
            LZWCompression.compress(fis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (var fis = new FileInputStream("destine.db"); var fos = new FileOutputStream("new_source.db")) {
            LZWCompression.decompress(fis, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int DICTIONARY_MAX_SIZE = 4095;

    private static List<Integer> generateLZWCodes(Map<List<Byte>, Integer> dictionary, byte[] bytes) {
        List<Integer> output = new LinkedList<>();
        int dictSize = 256;
        List<Byte> w = new LinkedList<>();

        for (byte b : bytes) {
            List<Byte> wb = new LinkedList<>(w);
            wb.add(b);

            if (dictionary.containsKey(wb))
                w.add(b);
            else {
                output.add(dictionary.get(w));

                if (dictSize < DICTIONARY_MAX_SIZE)
                    dictionary.put(wb, dictSize++);

                w = new LinkedList<>();
                w.add(b);
            }
        }

        if (!w.isEmpty())
            output.add(dictionary.get(w));

        return output;
    }

    private static Map<List<Byte>, Integer> initializeDictionary() {
        Map<List<Byte>, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++)
            dictionary.put(Collections.singletonList((byte) i), i);
        return dictionary;
    }

    public static void compress(FileInputStream src, FileOutputStream dst) {
        try {
            byte[] fileBytes = src.readAllBytes();

            Map<List<Byte>, Integer> dictionary = initializeDictionary();

            List<Integer> LZWCodes = generateLZWCodes(dictionary, fileBytes);

            byte[] compressed = BitCompressor.compressTo12Bits(LZWCodes);
            
            dst.write(compressed);
            dst.close();
            src.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decompress(FileInputStream src, FileOutputStream dst) {
        try {
            byte[] compressed = src.readAllBytes();

            List<Integer> LZWCodes = BitCompressor.decompressFrom12Bits(compressed);

            List<byte[]> dictionary = new ArrayList<>();
            for (int i = 0; i < 256; i++)
                dictionary.add(new byte[] { (byte) i });

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int prevCode = LZWCodes.get(0);
            baos.write(dictionary.get(prevCode));

            for (int i = 1; i < LZWCodes.size(); i++) {
                int currCode = LZWCodes.get(i);
                byte[] entry;
                if (currCode < dictionary.size())
                    entry = dictionary.get(currCode);
                else if (currCode == dictionary.size()) {
                    byte[] prevEntry = dictionary.get(prevCode);
                    entry = Arrays.copyOf(prevEntry, prevEntry.length + 1);
                    entry[entry.length - 1] = prevEntry[0];
                } else
                    throw new IllegalArgumentException("Invalid LZW code: " + currCode);

                baos.write(entry);

                byte[] prevEntry = dictionary.get(prevCode);
                byte[] newEntry = Arrays.copyOf(prevEntry, prevEntry.length + 1);
                newEntry[newEntry.length - 1] = entry[0];

                if (dictionary.size() < DICTIONARY_MAX_SIZE)
                    dictionary.add(newEntry);

                prevCode = currCode;
            }

            dst.write(baos.toByteArray());

            src.close();
            dst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
