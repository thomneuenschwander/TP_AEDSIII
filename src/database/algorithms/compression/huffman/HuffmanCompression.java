package database.algorithms.compression.huffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import database.algorithms.compression.Compression;

public class HuffmanCompression {

    public static void compress(FileInputStream src, ObjectOutputStream dst) {
        try {
            byte[] fileBytes = new byte[src.available()];
            src.read(fileBytes);
            src.close();

            Map<Byte, Integer> frequencies = Compression.calculateFrequencies(fileBytes);
            HuffmanNode root = createHuffmanTree(frequencies);

            Map<Byte, String> codeMap = new HashMap<>();
            generateCodes(root, new StringBuilder(), codeMap);
            byte[] huffmanEncoded = encode(fileBytes, codeMap);

            dst.writeObject(huffmanEncoded);
            dst.writeObject(codeMap);
            dst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HuffmanNode createHuffmanTree(Map<Byte, Integer> frequencies) {
        PriorityQueue<HuffmanNode> pq = getHuffmanRoots(frequencies);
        while (pq.size() > 1) {
            var left = pq.poll();
            var right = pq.poll();
            var sum = new HuffmanNode(null, left.frequency + right.frequency);
            sum.left = left;
            sum.right = right;
            pq.add(sum);
        }
        return pq.poll();
    }

    private static PriorityQueue<HuffmanNode> getHuffmanRoots(Map<Byte, Integer> frequencies) {
        PriorityQueue<HuffmanNode> roots = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet())
            roots.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        return roots;
    }

    private static void generateCodes(HuffmanNode root, StringBuilder code, Map<Byte, String> codeMap) {
        if (root.left == null && root.right == null) {
            codeMap.put(root.data, code.toString());
            return;
        }

        if (root.left != null) {
            code.append('0');
            generateCodes(root.left, code, codeMap);
            code.deleteCharAt(code.length() - 1);
        }

        if (root.right != null) {
            code.append('1');
            generateCodes(root.right, code, codeMap);
            code.deleteCharAt(code.length() - 1);
        }
    }

    private static byte[] encode(byte[] fileBytes, Map<Byte, String> codeMap) {
        StringBuilder bits = new StringBuilder();
        for (byte b : fileBytes)
            bits.append(codeMap.get(b));
        return bitPacking(bits);
    }

    private static byte[] bitPacking(StringBuilder bits) {
        int length = (int) Math.ceil(bits.length() / (double) Byte.SIZE);
        var bytes = new byte[length];

        for (int i = 0, j = 0; i < bits.length(); j++, i += Byte.SIZE) {
            String bitSegment = (i + 8 > bits.length()) ? bits.substring(i) : bits.substring(i, i + 8);
            bytes[j] = (byte) Integer.parseInt(bitSegment, 2);
        }
        return bytes;
    }

    public static void decompress(String inputFilePath, String outputFilePath) {
        try {
            var fis = new FileInputStream(inputFilePath);
            var ois = new ObjectInputStream(fis);
            byte[] compressed = (byte[]) ois.readObject();

            @SuppressWarnings("unchecked")
            Map<Byte, String> huffmanCodeMap = (Map<Byte, String>) ois.readObject();
            fis.close();

            StringBuilder bits = bitUnpacking(compressed);
            Map<String, Byte> reverseMap = reverseHuffmanCodeMap(huffmanCodeMap);
            List<Byte> decodedBytes = decodeBitsToBytes(bits, reverseMap);

            var fos = new FileOutputStream(outputFilePath);

            for (Byte b : decodedBytes)
                fos.write(b);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static StringBuilder bitUnpacking(byte[] bytes) {
        StringBuilder bits = new StringBuilder();
        for (byte b : bytes)
            bits.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        return bits;
    }

    private static Map<String, Byte> reverseHuffmanCodeMap(Map<Byte, String> huffmanCodeMap) {
        Map<String, Byte> reverseMap = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodeMap.entrySet())
            reverseMap.put(entry.getValue(), entry.getKey());
        return reverseMap;
    }

    private static List<Byte> decodeBitsToBytes(StringBuilder bits, Map<String, Byte> reverseMap) {
        List<Byte> decodedBytes = new ArrayList<>();
        StringBuilder currentBits = new StringBuilder();
        for (int i = 0; i < bits.length(); i++) {
            currentBits.append(bits.charAt(i));
            if (reverseMap.containsKey(currentBits.toString())) {
                decodedBytes.add(reverseMap.get(currentBits.toString()));
                currentBits.setLength(0);
            }
        }
        return decodedBytes;
    }
}
