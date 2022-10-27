package week11;

public class CircularSuffixArray {


    private final int[] indexes;


    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }

        int len = s.length();
        final CircularSuffix[] suffix = new CircularSuffix[len];
        indexes = new int[len];

        int offset = len;

        for (int i = 0; i < len; i++) {
            suffix[i] = new CircularSuffix(s, offset % len);
            offset--;
        }

        sort(suffix, len);
        populateIndexes(suffix);
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

    private void sort(CircularSuffix[] a, int w) {
        int n = a.length;
        int R = 256;   // extend ASCII alphabet size
        CircularSuffix[] aux = new CircularSuffix[n];

        for (int d = w - 1; d >= 0; d--) {
            // sort by key-indexed counting on dth character

            // compute frequency counts
            int[] count = new int[R + 1];
            for (CircularSuffix suffix : a) count[suffix.charAt(d) + 1]++;

            // compute cumulates
            for (int r = 0; r < R; r++)
                count[r + 1] += count[r];

            // move data
            for (CircularSuffix circularSuffix : a) aux[count[circularSuffix.charAt(d)]++] = circularSuffix;

            // copy back
            System.arraycopy(aux, 0, a, 0, n);
        }
    }

    private static class CircularSuffix {

        private final String originalValue;
        private final int offset;

        private CircularSuffix(String originalValue, int offset) {
            this.originalValue = originalValue;
            this.offset = offset;
        }

        public char charAt(int index) {
            return originalValue
                    .charAt((index + offset) % originalValue.length());
        }

        public int getOffset() {
            return offset;
        }

        @Override
        public String toString() {
            return "CircularSuffix{" + "offset=" + offset +
                    '}';
        }
    }


    private void populateIndexes(CircularSuffix[] suffixArrays) {
        for (int i = 0; i < suffixArrays.length; i++) {
            indexes[i] = suffixArrays[i].getOffset();
        }
    }


    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray suffixArray = new CircularSuffixArray("ABRACADABRA!");
        suffixArray.length();
    }
}
