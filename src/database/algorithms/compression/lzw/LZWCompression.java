package database.algorithms.compression.lzw;

import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LZWCompression {
    private static final int DICTIONARY_MAX_SIZE = 4096;
    private static final int BASICS_SYMBOLS_SIZE = 256;
   // private static final int BITS_PER_INDEX = 12;
    private static final int BYTE_MASK = 0xFF;

    public static void compress(FileInputStream src, ObjectOutputStream dst) {
        try {
            byte[] fileBytes = new byte[src.available()];
            src.read(fileBytes);
            src.close();

            Map<String, Integer> dictionary = initializeDictionary();
            List<Integer> LZWCodes = generateLZWCodes(fileBytes, dictionary);
            
            dst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Integer> initializeDictionary() {
        Map<String, Integer> dictionary = new LinkedHashMap<>(BASICS_SYMBOLS_SIZE);
        for (int i = 0; i < BASICS_SYMBOLS_SIZE; i++)
            dictionary.put("" + (char) i, i);
        return dictionary;
    }

    private static List<Integer> generateLZWCodes(byte[] bytes, Map<String, Integer> dictionary) {
        int dictSize = BASICS_SYMBOLS_SIZE;
        String prefix = "";
        List<Integer> output = new ArrayList<>();

        for (byte b : bytes) {
            char symbol = (char) (b & BYTE_MASK);
            String prefixPlusSymbol = prefix + symbol;

            if (dictionary.containsKey(prefixPlusSymbol))
                prefix = prefixPlusSymbol;
            else {
                output.add(dictionary.get(prefix));
                if (dictSize < DICTIONARY_MAX_SIZE) 
                    dictionary.put(prefixPlusSymbol, dictSize++);
                prefix = "" + symbol;
            }
        }

        if (!prefix.isEmpty()) 
            output.add(dictionary.get(prefix));

        return output;
    }
}
