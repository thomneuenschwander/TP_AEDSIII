package database.algorithms.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

import database.domain.Cryptography;

public class RSA2 implements Cryptography{
    private BigInteger n, d, e;

    private int bitlen = 1024;

    public RSA2(BigInteger e, BigInteger d, BigInteger n) {
        this.e = e;
        this.d = d;
        this.n = n;
    }

    public RSA2(int bits) {
        bitlen = bits;
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(bitlen / 2, 100, r);
        BigInteger q = new BigInteger(bitlen / 2, 100, r);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("65537"); 
        d = e.modInverse(phi);
    }
    
    @Override
    public synchronized byte[] encrypt(byte[] raw) {
        return (new BigInteger(raw)).modPow(e, n).toByteArray();
    }

    @Override
    public synchronized byte[] decrypt(byte[] encrypted) {
        return (new BigInteger(encrypted)).modPow(d, n).toByteArray();
    }

    public static void main(String[] args) {
        RSA2 rsa = new RSA2(1024);

        String teststring = "Hello, RSA!";
        System.out.println("Original: " + teststring);

        byte[] encrypted = rsa.encrypt(teststring.getBytes());
        System.out.println("Encrypted: " + new String(encrypted));

        byte[] decrypted = rsa.decrypt(encrypted);
        System.out.println("Decrypted: " + new String(decrypted));
    }

    @Override
    public String getAlgorithmName() {
        return "RSA" + this.bitlen + "bits";
    }
}
