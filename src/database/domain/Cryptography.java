package database.domain;

public interface Cryptography {
    byte[] encrypt(byte[] rawMessage);

    byte[] decrypt(byte[] encrypted);

    String getAlgorithmName(); 
}
