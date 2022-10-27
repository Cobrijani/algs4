public class CircularSuffixArray {
    private static final int R = 256;   // extended ASCII alphabet size
    private static final int CUTOFF = 15;   // cutoff to insertion sort
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
        msdSort(suffix);

        for (int i = 0; i < suffix.length; i++) {
            indexes[i] = suffix[i].getOffset();
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

        public int length() {
            return originalValue.length();
        }

        @Override
        public String toString() {
            return "CircularSuffix{" + "offset=" + offset +
                    '}';
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

    private static void msdSort(CircularSuffix[] a) {
        int n = a.length;
        CircularSuffix[] aux = new CircularSuffix[n];
        sort(a, 0, n - 1, 0, aux);
    }

    private static int charAt(CircularSuffix s, int d) {
        return s.charAt(d);
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private static void sort(CircularSuffix[] a, int lo, int hi, int d, CircularSuffix[] aux) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        // compute frequency counts
        int[] count = new int[R + 2];
        for (int i = lo; i <= hi; i++) {
            int c = charAt(a[i], d);
            count[c + 2]++;
        }

        // transform counts to indicies
        for (int r = 0; r < R + 1; r++)
            count[r + 1] += count[r];

        // distribute
        for (int i = lo; i <= hi; i++) {
            int c = charAt(a[i], d);
            aux[count[c + 1]++] = a[i];
        }

        // copy back
        for (int i = lo; i <= hi; i++)
            a[i] = aux[i - lo];


        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < R; r++)
            sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
    }


    // insertion sort a[lo..hi], starting at dth character
    private static void insertion(CircularSuffix[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j - 1], d); j--)
                exch(a, j, j - 1);
    }

    // exchange a[i] and a[j]
    private static void exch(CircularSuffix[] a, int i, int j) {
        CircularSuffix temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    // is v less than w, starting at character d
    private static boolean less(CircularSuffix v, CircularSuffix w, int d) {
        for (int i = d; i < Math.min(v.length(), w.length()); i++) {
            if (v.charAt(i) < w.charAt(i)) return true;
            if (v.charAt(i) > w.charAt(i)) return false;
        }
        return v.length() < w.length();
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray suffixArray = new CircularSuffixArray("ABRACADABRA!");
        suffixArray.length();
    }
}
