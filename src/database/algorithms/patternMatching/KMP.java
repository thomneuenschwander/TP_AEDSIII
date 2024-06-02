package database.algorithms.patternMatching;

public class KMP {

    private static int[] computePrefixTable(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int j = 0; 
        int i = 1;

        lps[0] = 0; 

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(j)) 
                lps[i++] = ++j;
            else {
                if (j != 0) 
                    j = lps[j - 1];
                else 
                    lps[i++] = j; 
            }
        }
        return lps;
    }

    public static int matchPatternAt(String text, String pattern) {
        int count = 0;
        int n = text.length();
        int m = pattern.length();
        int[] lps = computePrefixTable(pattern);

        int i = 0; 
        int j = 0; 

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                j++;
                i++;
            }
            if (j == m) {
                count++;
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return count;
    }

    public static boolean search(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        int[] lps = computePrefixTable(pattern);

        int i = 0; 
        int j = 0; 

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                j++;
                i++;
            }
            if (j == m) {
                return true;
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
        return false;
    }
}