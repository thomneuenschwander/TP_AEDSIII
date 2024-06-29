package database.algorithms.cryptography;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

import database.domain.Cryptography;

public class RSA implements Cryptography{

    private BigInteger n, d, e;

    public RSA(int bitLength) {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitLength / 2, random);

        while (phi.gcd(e).intValue() > 1) {
            e = e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
    }

    @Override
    public byte[] encrypt(byte[] message) {
        return (new BigInteger(message)).modPow(e, n).toByteArray();
    }

    @Override
    public byte[] decrypt(byte[] encrypted) {
        return (new BigInteger(encrypted)).modPow(d, n).toByteArray();
    }

    public static void encryptFile(RSA rsa, String inputFilePath, String outputFilePath) throws IOException {
        File inputFile = new File(inputFilePath);
        File encryptedFile = new File(outputFilePath);
        try (FileInputStream fis = new FileInputStream(inputFile);
                FileOutputStream fos = new FileOutputStream(encryptedFile);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {
            int b;
            while ((b = fis.read()) != -1) {
                byte[] encrypted = rsa.encrypt(new byte[] { (byte) b });
                String encoded = Base64.getEncoder().encodeToString(encrypted);
                writer.write(encoded);
                writer.newLine();
            }
        }
    }

    public static void decryptFile(RSA rsa, String inputFilePath, String outputFilePath) throws IOException {
        File encryptedFile = new File(inputFilePath);
        File decryptedFile = new File(outputFilePath);
        try (FileInputStream fis = new FileInputStream(encryptedFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                FileOutputStream fos = new FileOutputStream(decryptedFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                byte[] encrypted = Base64.getDecoder().decode(line);
                byte[] decrypted = rsa.decrypt(encrypted);
                fos.write(decrypted);
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "RSA";
    }
}
