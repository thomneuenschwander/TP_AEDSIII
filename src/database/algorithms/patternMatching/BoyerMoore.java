package database.algorithms.patternMatching;

import java.util.HashMap;
import java.util.Map;

public class BoyerMoore {
    public static int matchPatternAt(byte[] text, byte[] pattern) {
        if (text == null || pattern == null)
            throw new IllegalArgumentException("Neither text nor pattern can be null.");

        int n = text.length;
        int m = pattern.length;

        if (n < m)
            return -1;

        Map<Byte, Integer> badCharShift = calculateBadCharShift(pattern);
        int[] goodSuffixShift = calculateGoodSuffixShift(pattern);

        int i = m - 1;
        while (i < n) {
            int j = m - 1;
            int p = i;
            for (; j >= 0 && pattern[j] == text[p]; j--, p--)
                ;
            if (j < 0)
                return i - m + 1;

            int bcs = j - badCharShift.getOrDefault(text[p], -1);
            int gss = goodSuffixShift[j];

            int shift = Math.max(bcs, gss);
            i += shift;
        }

        return -1;
    }

    private static Map<Byte, Integer> calculateBadCharShift(byte[] pattern) {
        int m = pattern.length;
        Map<Byte, Integer> badCharShift = new HashMap<>();

        for (int i = 0; i < m - 1; i++)
            badCharShift.put(pattern[i], i);

        return badCharShift;
    }

    private static int[] calculateGoodSuffixShift(byte[] pattern) {
        int m = pattern.length;
        int[] goodSuffixShift = new int[m];
        int lastPrefixPosition = m;

        for (int i = m - 1; i >= 0; i--) {
            if (isPrefix(pattern, i + 1))
                lastPrefixPosition = i + 1;
            goodSuffixShift[i] = lastPrefixPosition + (m - 1 - i);
        }

        for (int i = 0; i < m - 1; i++) {
            int slen = suffixLength(pattern, i);
            goodSuffixShift[m - 1 - slen] = m - 1 - i + slen;
        }

        return goodSuffixShift;
    }

    private static boolean isPrefix(byte[] pattern, int p) {
        int m = pattern.length;
        for (int i = p, j = 0; i < m; i++, j++) {
            if (pattern[i] != pattern[j])
                return false;
        }
        return true;
    }

    private static int suffixLength(byte[] pattern, int p) {
        int m = pattern.length;
        int len = 0;
        for (int i = p, j = m - 1; i >= 0 && pattern[i] == pattern[j]; i--, j--) 
            len++;
        return len;
    }
}
