package database.algorithms.patternMatching;

public class Naive {

    public static int matchPattern(String text, String pattern) {
        int m = pattern.length();
        int n = text.length();
        int count = 0;

        for (int i = 0; i <= n - m; i++) {
            int j;

            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) 
                    break; 
            }
            
            if (j == m) 
                count++; 
        }

        return count;
    }

    public static boolean search(String text, String pattern) {
        int m = pattern.length();
        int n = text.length();

        for (int i = 0; i <= n - m; i++) {
            int j;

            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) 
                    break; 
            }
            
            if (j == m) 
                return true; 
        }

        return false;
    }
}
