package application.model.enums;

public enum CompressionAlgorithm {
    HUFFMAN,
    LZW;

    public String getDescription() {
        switch (this) {
            case HUFFMAN:
                return "Huffman Coding: A lossless data compression algorithm.";
            case LZW:
                return "LZW: Lempel-Ziv-Welch compression algorithm.";
            default:
                return "Unknown algorithm.";
        }
    }
}
