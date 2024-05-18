package database.algorithms.compression.huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {
    protected Byte data;
    protected int frequency;
    protected HuffmanNode left;
    protected HuffmanNode right;

    public HuffmanNode(Byte data, int frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
        this.data = '\0';
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    

    @Override
    public int compareTo(HuffmanNode node) {
        return this.frequency - node.frequency;
    }
}
