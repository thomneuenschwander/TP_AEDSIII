package database.algorithms.entropy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

import database.domain.persistence.FileType;

public class Entropy {
    private File source;
    private File destine;
    private double entropy;
    private long duration;
    private double compressionPercentage;
    private boolean loggingEnabled = false;
    private FileType fileType;

    public Entropy setCompressionFileType(FileType fileType) {
        this.fileType = fileType;
        return this;
    }

    public Entropy setSource(String filePath) throws IOException {
        this.source = (this.fileType == null) ? new File(filePath + "db")
                : new File(filePath + this.fileType.getExtension());
        if (!this.source.exists())
            this.source.createNewFile();
        return this;
    }

    public Entropy setDestine(String filePath) throws IOException {
        this.destine = (this.fileType == null) ? new File(filePath + "db")
                : new File(filePath + this.fileType.getExtension());
        if (!this.destine.exists())
            this.destine.createNewFile();
        return this;
    }

    public Entropy enableLogging(boolean enable) {
        this.loggingEnabled = enable;
        return this;
    }

    public Entropy zip(BiConsumer<FileInputStream, FileOutputStream> compressionMethod) {
        try (var src = new FileInputStream(source);
                var dst = new FileOutputStream(destine)) {

            long startTime = System.nanoTime();
            compressionMethod.accept(src, dst);
            long endTime = System.nanoTime();

            this.duration = endTime - startTime;

            long originalSize = this.source.length();
            long compressedSize = this.destine.length();
            this.compressionPercentage = ((double) (originalSize - compressedSize) / originalSize) * 100;

            FileInputStream compressed = new FileInputStream(destine);
            this.entropy = calculateEntropy(compressed);

            if (loggingEnabled)
                loggin(System.out, originalSize, compressedSize);
        } catch (Exception e) {
            if (loggingEnabled)
                System.out.println("Compression failed");
            e.printStackTrace();
        }
        return this;
    }

    public Entropy unzip(BiConsumer<FileInputStream, FileOutputStream> decompressionMethod) {
        try (var src = new FileInputStream(source);
                var dst = new FileOutputStream(destine)) {

            long startTime = System.nanoTime();
            decompressionMethod.accept(src, dst);
            long endTime = System.nanoTime();

            this.duration = endTime - startTime;

            FileInputStream decompressed = new FileInputStream(destine);
            this.entropy = calculateEntropy(decompressed);

            if (loggingEnabled)
                loggin(System.out, source.length(), destine.length());
        } catch (Exception e) {
            if (loggingEnabled)
                System.out.println("Decompression failed");
            e.printStackTrace();
        }
        return this;
    }

    public double getEntropy() {
        return entropy;
    }

    public long getDuration() {
        return duration;
    }

    public double getCompressionPercentage() {
        return compressionPercentage;
    }

    public File getSource() {
        return source;
    }

    public File getDestine() {
        return destine;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    private void loggin(PrintStream log, long originalSize, long finalSize) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('.');

        DecimalFormat durationFormat = new DecimalFormat("#,##0.000", symbols);
        DecimalFormat sizeFormat = new DecimalFormat("#,##0", symbols);
        DecimalFormat percentageFormat = new DecimalFormat("#,##0.00", symbols);
        DecimalFormat entropyFormat = new DecimalFormat("#,##0.0000", symbols);

        log.println("Operation took " + durationFormat.format(duration / 1_000_000.0) + " milliseconds");
        log.println("Original size: " + sizeFormat.format(originalSize) + " bytes");
        log.println("Final size: " + sizeFormat.format(finalSize) + " bytes");
        if (compressionPercentage != 0) {
            log.println("Compression percentage: " + percentageFormat.format(compressionPercentage) + "%");
            log.println("Entropy of compressed file: " + entropyFormat.format(entropy));
        }
        System.out.println();
    }

    public static double calculateEntropy(FileInputStream fis) {
        try {
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
            return calculateEntropy(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public static double calculateEntropy(byte[] data) {
        Map<Byte, Integer> frequencies = calculateFrequencies(data);

        double entropy = 0.0;
        int dataLength = data.length;

        for (Map.Entry<Byte, Integer> entry : frequencies.entrySet()) {
            double frequency = (double) entry.getValue() / dataLength;
            entropy += frequency * log2(frequency);
        }

        return -entropy;
    }

    public static Map<Byte, Integer> calculateFrequencies(byte[] bytes) {
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : bytes)
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        return frequencies;
    }

    public static double log2(double number) {
        return Math.log(number) / Math.log(2);
    }
}
