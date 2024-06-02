package database.algorithms.patternMatching;

import java.util.HashMap;
import java.util.Map;

public class BoyerMoore {
    public static int matchPatternAt(String text, String pattern) {
        if (text == null || pattern == null)
            throw new IllegalArgumentException("Neither text nor pattern can be null.");

        int n = text.length();
        int m = pattern.length();

        if (n < m)
            return -1;

        Map<Character, Integer> badCharShift = calculateBadCharShift(pattern);
        int[] goodSuffixShift = calculateGoodSuffixShift(pattern);

        int i = m - 1;
        while (i < n) {
            int j = m - 1;
            int p = i;
            for (; j >= 0 && pattern.charAt(j) == text.charAt(p); j--, p--)
                ;
            if (j < 0)
                return i - m + 1;

            int bcs = j - badCharShift.getOrDefault(text.charAt(p), -1);
            int gss = goodSuffixShift[j];

            int shift = bcs >= gss ? bcs : gss;
            i += shift;
        }

        return -1;
    }

    private static Map<Character, Integer> calculateBadCharShift(String pattern) {
        Map<Character, Integer> badCharShift = new HashMap<>();
        for (int i = 0; i < pattern.length() - 1; i++)
            badCharShift.put(pattern.charAt(i), i);
        return badCharShift;
    }

    private static int[] calculateGoodSuffixShift(String pattern) {
        int m = pattern.length();
        int[] goodSuffixShift = new int[m];
        int[] suffixes = new int[m];

        suffixes[m - 1] = m;
        for (int i = m - 2; i >= 0; i--) {
            int j = i;
            while (j >= 0 && pattern.charAt(j) == pattern.charAt(m - 1 - i + j))
                j--;

            suffixes[i] = i - j;
        }

        for (int i = 0; i < m; i++)
            goodSuffixShift[i] = m;

        int j = 0;
        for (int i = m - 1; i >= 0; i--) {
            if (i + 1 == suffixes[i]) {
                for (; j < m - 1 - i; j++) {
                    if (goodSuffixShift[j] == m)
                        goodSuffixShift[j] = m - 1 - i;
                }
            }
        }

        for (int i = 0; i <= m - 2; i++)
            goodSuffixShift[m - 1 - suffixes[i]] = m - 1 - i;

        return goodSuffixShift;
    }
}
