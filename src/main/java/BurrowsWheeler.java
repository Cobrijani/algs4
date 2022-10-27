import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.LSD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        while (!BinaryStdIn.isEmpty()) {
            final String next = BinaryStdIn.readString();

            CircularSuffixArray circularSuffixArray = new CircularSuffixArray(next);


            int first = -1;
            char[] lastRow = new char[next.length()];
            for (int i = 0; i < next.length(); i++) {
                int nextIndex = circularSuffixArray.index(i);
                if (nextIndex == 0) {
                    first = i;
                }
                lastRow[i] = next
                        .charAt((next.length() - 1 + nextIndex) % next.length());
            }
            BinaryStdOut.write(first, 32);
            for (char c : lastRow) {
                BinaryStdOut.write(c, 8);
            }
        }
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt(32);
        final List<Integer> codes = new ArrayList<>();
        while (!BinaryStdIn.isEmpty()) {
            codes.add(BinaryStdIn.readInt(8));
        }
        inverseTransform(first, codes);
        codes.clear();
    }

    private static void inverseTransform(int first, List<Integer> codes) {
        int[] t = new int[codes.size()];
        for (int i = 0; i < codes.size(); i++) {
            t[i] = codes.get(i);
        }
        int[] sortedT = Arrays.copyOf(t, t.length);
        LSD.sort(sortedT);

        int[] next = constructNext(t, sortedT);

        int nextIndex = first;
        for (int i = 0; i < next.length; i++) {
            BinaryStdOut.write((char) sortedT[nextIndex]);
            nextIndex = next[nextIndex];
        }
        BinaryStdOut.flush();
    }


    private static int[] constructNext(int[] t, int[] firstColumn) {
        int[] next = new int[t.length];

        boolean[] marked = new boolean[t.length];
        Arrays.fill(next, -1);
        Arrays.fill(marked, false);

        for (int i = 0; i < next.length; i++) {
            int first = firstColumn[i];
            for (int j = 0; j < t.length; j++) {
                int lastChar = t[j];

                if (first == lastChar && !marked[j]) {
                    next[i] = j;
                    marked[j] = true;
                    break;
                }
            }
        }
        return next;
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if ("-".equals(args[0])) {
            BurrowsWheeler.transform();
        }
        if ("+".equals(args[0])) {
            BurrowsWheeler.inverseTransform();
        }
    }
}
