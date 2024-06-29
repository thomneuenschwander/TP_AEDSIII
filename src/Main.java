import database.RepositoryImpl02;
import database.algorithms.cryptography.RSA;
import database.algorithms.patternMatching.BoyerMoore;

public class Main {
    static final String DIR = "src\\database\\data\\";

    static final String C = DIR + "cryptography\\plain.txt";
    static final String D = DIR + "cryptography\\encrypted_RSA.txt";
    static final String E = DIR + "cryptography\\decrypted_RSA.txt";

    public static void main(String[] args) throws Exception {
        RepositoryImpl02 repository = new RepositoryImpl02();

        String pattern = "Schenectady";
        boolean match = repository.searchPattern(BoyerMoore::matchPatternAt, pattern.getBytes(), DIR + "data.db");

        System.out.println(match);

        RSA rsa = new RSA(1024);

        RSA.encryptFile(rsa, C, D);

        RSA.decryptFile(rsa, D, E);
    }
}
