package database.algorithms.compression.huffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanCompression {

    private Map<Byte, String> codeMap = new HashMap<>();

    public void compress(String inputFilePath, String outputFilePath) {
        try {
            var fis = new FileInputStream(inputFilePath);
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
            fis.close();

            Map<Byte, Integer> frequencies = calculateFrequencies(fileBytes);
            HuffmanNode root = createHuffmanTree(frequencies);

            generateCodes(root, new StringBuilder());
            byte[] huffmanEncoded = encode(fileBytes);

            var oos = new ObjectOutputStream(new FileOutputStream(outputFilePath));
            oos.writeObject(huffmanEncoded);
            oos.writeObject(huffmanEncoded);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<Byte, Integer> calculateFrequencies(byte[] bytes) {
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : bytes)
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        return frequencies;
    }

    private HuffmanNode createHuffmanTree(Map<Byte, Integer> frequencies) {
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

    private PriorityQueue<HuffmanNode> getHuffmanRoots(Map<Byte, Integer> frequencies) {
        PriorityQueue<HuffmanNode> roots = new PriorityQueue<>();
        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet())
            roots.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        return roots;
    }

    private void generateCodes(HuffmanNode root, StringBuilder code) {
        if (root.left == null && root.right == null) {
            this.codeMap.put(root.data, code.toString());
            return;
        }

        if (root.left != null) {
            code.append('0');
            generateCodes(root.left, code);
            code.deleteCharAt(code.length() - 1);
        }

        if (root.right != null) {
            code.append('1');
            generateCodes(root.right, code);
            code.deleteCharAt(code.length() - 1);
        }
    }

    private byte[] encode(byte[] fileBytes) {
        StringBuilder bits = new StringBuilder();
        for (byte b : fileBytes)
            bits.append(this.codeMap.get(b));
        return bitPacking(bits);
    }

    private byte[] bitPacking(StringBuilder bits) {
        int length = (int) Math.ceil(bits.length() / (double) Byte.SIZE);
        var bytes = new byte[length];

        for (int i = 0, j = 0; i < bits.length(); j++, i += Byte.SIZE) {
            String bitSegment = (i + 8 > bits.length()) ? bits.substring(i) : bits.substring(i, i + 8);
            bytes[j] = (byte) Integer.parseInt(bitSegment, 2);
        }
        return bytes;
    }
}
