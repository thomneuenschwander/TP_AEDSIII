package database.algorithms.cryptography;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import database.domain.Cryptography;

public class TranspositionCipher implements Cryptography {
    private final String key;

    public TranspositionCipher(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public synchronized byte[] encrypt(byte[] raw) {
        Map<Character, List<Byte>> table = generateTable(raw);
        List<Byte> encodedMessage = new ArrayList<>();

        TreeMap<Character, List<Byte>> sortedTable = new TreeMap<>(table);

        for (List<Byte> column : sortedTable.values()) {
            for (Byte b : column) 
                encodedMessage.add(b != null ? b : (byte) ' ');
        }

        byte[] result = new byte[encodedMessage.size()];
        for (int i = 0; i < encodedMessage.size(); i++) {
            result[i] = encodedMessage.get(i);
        }

        return result;
    }

    @Override
    public synchronized byte[] decrypt(byte[] encrypted) {
        int colCount = key.length();
        int rowCount = (int) Math.ceil((double) encrypted.length / colCount);

        Map<Character, List<Byte>> sortedTable = new TreeMap<>();
        for (char ch : key.toCharArray()) 
            sortedTable.put(ch, new ArrayList<>(Collections.nCopies(rowCount, null)));

        int index = 0;
        for (var entry : sortedTable.entrySet()) {
            for (int i = 0; i < rowCount; i++) {
                if (index < encrypted.length)
                    entry.getValue().set(i, encrypted[index++]);
            }
        }

        Map<Character, List<Byte>> table = new LinkedHashMap<>();
        for (char ch : key.toCharArray()) 
            table.put(ch, sortedTable.get(ch));

        List<Byte> decodedMessage = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            for (var values : table.values()) {
                if (i < values.size() && values.get(i) != null) 
                    decodedMessage.add(values.get(i));
            }
        }

        byte[] result = new byte[decodedMessage.size()];
        for (int i = 0; i < decodedMessage.size(); i++) 
            result[i] = decodedMessage.get(i);

        return result;
    }

    private Map<Character, List<Byte>> generateTable(byte[] raw) {
        int colCount = key.length();
        int rowCount = (int) Math.ceil((double) raw.length / colCount);
        Map<Character, List<Byte>> table = new LinkedHashMap<>();

        for (char ch : key.toCharArray()) 
            table.put(ch, new ArrayList<>(rowCount));

        for (int i = 0; i < raw.length;) {
            for (var values : table.values()) {
                if (i < raw.length) 
                    values.add(raw[i++]);
                else 
                    values.add(null); 
            }
        }

        return table;
    }

    @Override
    public String getAlgorithmName() {
        return "TranspositionCipher";
    }
}
