package database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import database.algorithms.entropy.Entropy;
import database.domain.Cryptography;
import database.domain.Repository;
import database.domain.persistence.FileType;

public class RepositoryImpl02 implements Repository {

    public byte[] encrypt(Cryptography cryptography, String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] rawFile = bis.readAllBytes();
            return encrypt(cryptography, rawFile, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encrypt(Cryptography cryptography, byte[] rawData, String filePath) {
        String encryptedFilePath = filePath + "_encrypted_" + cryptography.getAlgorithmName() + ".db";
        byte[] encryptedData = cryptography.encrypt(rawData);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(encryptedFilePath))) {
            bos.write(encryptedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encryptedData;
    }

    public byte[] decrypt(Cryptography cryptography, String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] encryptedFile = bis.readAllBytes();
            return decrypt(cryptography, encryptedFile, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decrypt(Cryptography cryptography, byte[] encryptedData, String filePath) {
        String decryptedFilePath = filePath + "_decrypted_" + cryptography.getAlgorithmName() + ".db";
        byte[] decryptedData = cryptography.decrypt(encryptedData);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decryptedFilePath))) {
            bos.write(decryptedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return decryptedData;
    }

    public File compressFile(BiConsumer<FileInputStream, FileOutputStream> compressionMethod, String src) {
        try {
            return new Entropy()
                    .enableLogging(false)
                    .setCompressionFileType(FileType.DB)
                    .setSource(src)
                    .setDestine(src + "_" + compressionMethod.getClass().getName())
                    .unzip(compressionMethod).getDestine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File decompressFile(BiConsumer<FileInputStream, FileOutputStream> decompressionMethod, String src) {
        try {
            return new Entropy()
                    .enableLogging(false)
                    .setCompressionFileType(FileType.DB)
                    .setSource(src)
                    .setDestine(src + "_" + decompressionMethod.getClass().getName())
                    .unzip(decompressionMethod).getDestine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean searchPattern(BiFunction<byte[], byte[], Integer> matcher, byte[] pattern, String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] file = bis.readAllBytes();
            return matcher.apply(file, pattern) != -1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}