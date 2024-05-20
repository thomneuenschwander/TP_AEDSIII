package database.algorithms.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

public class Compression {
    private File source;
    private File destine;
    private double entropy;
    private long duration;
    private double compressionPercentage;
    private boolean loggingEnabled = false;

    public Compression setSource(String filePath) {
        this.source = new File(filePath);
        return this;
    }

    public Compression setDestine(String filePath) {
        this.destine = new File(filePath);
        return this;
    }

    public Compression enableLogging(boolean enable) {
        this.loggingEnabled = enable;
        return this;
    }

    public Compression zip(BiConsumer<FileInputStream, ObjectOutputStream> compressionMethod) {
        long startTime = System.nanoTime();
        try (FileInputStream src = new FileInputStream(source);
                ObjectOutputStream dst = new ObjectOutputStream(new FileOutputStream(destine))) {
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
                System.out.println("Compression faild");
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

    private void loggin(PrintStream log, long originalSize, long compressedSize) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('.');

        DecimalFormat durationFormat = new DecimalFormat("#,##0.000", symbols);
        DecimalFormat sizeFormat = new DecimalFormat("#,##0", symbols);
        DecimalFormat percentageFormat = new DecimalFormat("#,##0.00", symbols);
        DecimalFormat entropyFormat = new DecimalFormat("#,##0.0000", symbols);

        log.println("Compression took " + durationFormat.format(duration / 1_000_000.0) + " milliseconds");
        log.println("Original size: " + sizeFormat.format(originalSize) + " bytes");
        log.println("Compressed size: " + sizeFormat.format(compressedSize) + " bytes");
        log.println("Compression percentage: " + percentageFormat.format(compressionPercentage) + "%");
        log.println("Entropy of compressed file: " + entropyFormat.format(entropy));
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
            entropy += frequency * (Math.log(frequency) / Math.log(2));
        }

        return -entropy;
    }

    public static Map<Byte, Integer> calculateFrequencies(byte[] bytes) {
        Map<Byte, Integer> frequencies = new HashMap<>();
        for (byte b : bytes)
            frequencies.put(b, frequencies.getOrDefault(b, 0) + 1);
        return frequencies;
    }
}
