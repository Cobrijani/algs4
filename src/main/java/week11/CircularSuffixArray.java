package week11;

import edu.princeton.cs.algs4.Quick3string;

import java.util.HashMap;
import java.util.Map;

public class CircularSuffixArray {


    private final int[] indexes;


    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }

        int len = s.length();
        final String[] suffixArrays = new String[len];
        indexes = new int[len];

        int offset = len;


        for (int i = 0; i < suffixArrays.length; i++) {
            char[] chars = new char[len];
            for (int j = 0; j < suffixArrays.length; j++) {
                int index = (j + offset) % len;
                chars[index] = s.charAt(j);
            }
            suffixArrays[i] = String.valueOf(chars);
            offset--;
        }

        final Map<String, Integer> indexMap = new HashMap<>();

        for (int i = 0; i < suffixArrays.length; i++) {
            String val = suffixArrays[i];
            indexMap.put(val, i);
        }
        Quick3string.sort(suffixArrays);
        populateIndexes(suffixArrays, indexMap);
    }


    private void populateIndexes(String[] suffixArrays, Map<String, Integer> indexMap) {
        for (int i = 0; i < suffixArrays.length; i++) {
            String val = suffixArrays[i];
            int orig = indexMap.get(val);
            indexes[i] = orig;
        }
    }

    // length of s
    public int length() {
        return indexes.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > length() - 1) {
            throw new IllegalArgumentException();
        }
        return indexes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray suffixArray = new CircularSuffixArray("ABRACADABRA!");
        suffixArray.length();
    }
}
