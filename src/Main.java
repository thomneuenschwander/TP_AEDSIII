import database.algorithms.entropy.Entropy;
import database.algorithms.entropy.huffman.HuffmanCompression;
import database.domain.FileType;

public class Main {
    static final String COMPRESSION_SRC_PATH = "src\\database\\data\\data";

    static final String HUFFMAN_COMPRESSION_DST_PATH = "src\\database\\data\\entropy\\HUFFMAN_compressed";
    static final String HUFFMAN_DECOMPRESSION_SRC_PATH = HUFFMAN_COMPRESSION_DST_PATH;
    static final String HUFFMAN_DECOMPRESSION_DST_PATH = "src\\database\\data\\entropy\\HUFFMAN_decompressed";

    static final String LZW_COMPRESSION_DST_PATH = "src\\database\\data\\entropy\\LZW_compressed";
    static final String LZW_DECOMPRESSION_SRC_PATH = LZW_COMPRESSION_DST_PATH;
    static final String LZW_DECOMPRESSION_DST_PATH = "src\\database\\data\\entropy\\LZW_decompressed";

    public static void main(String[] args) throws Exception {

        // HUFFMAN COMPRESSION AND DECOMPRESSION

        var huffmanEncoding = new Entropy()
                .enableLogging(false)
                .setCompressionFileType(FileType.DB)
                .setSource(COMPRESSION_SRC_PATH)
                .setDestine(HUFFMAN_COMPRESSION_DST_PATH)
                .zip(HuffmanCompression::compress);

        var huffmanDecoding = new Entropy()
                .enableLogging(false)
                .setCompressionFileType(FileType.DB)
                .setSource(HUFFMAN_DECOMPRESSION_SRC_PATH)
                .setDestine(HUFFMAN_DECOMPRESSION_DST_PATH)
                .unzip(HuffmanCompression::decompress);

        // LZW COMPRESSION AND DECOMPRESSION

        var lzwEncoding = new Entropy()
                .enableLogging(false)
                .setCompressionFileType(FileType.DB)
                .setSource(COMPRESSION_SRC_PATH)
                .setDestine(LZW_COMPRESSION_DST_PATH)
                .zip(HuffmanCompression::compress);

        var lzwDecoding = new Entropy()
                .enableLogging(false)
                .setCompressionFileType(FileType.DB)
                .setSource(LZW_DECOMPRESSION_SRC_PATH)
                .setDestine(LZW_DECOMPRESSION_DST_PATH)
                .unzip(HuffmanCompression::decompress);

        System.out.println("Huffman Compression Summary:");
        printSummary(huffmanEncoding);
        System.out.println("Huffman Decompression Summary:");
        printSummary(huffmanDecoding);

        System.out.println("LZW Compression Summary:");
        printSummary(lzwEncoding);
        System.out.println("LZW Decompression Summary:");
        printSummary(lzwDecoding);
    }

    private static void printSummary(Entropy entropy) {
        System.out.println("Source File: " + entropy.getSource().getPath());
        System.out.println("Destination File: " + entropy.getDestine().getPath());
        System.out.println("Duration: " + entropy.getDuration() + " nanoseconds");
        System.out.println("Compression Percentage: " + entropy.getCompressionPercentage() + "%");
        System.out.println("Entropy: " + entropy.getEntropy());
        System.out.println();
    }
}
