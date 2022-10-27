import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
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
        int[] next = constructNext(codes);

        int nextIndex = first;
        for (int i = 0; i < next.length; i++) {
            nextIndex = next[nextIndex];
            BinaryStdOut.write((char) codes.get(nextIndex).intValue());
        }
        BinaryStdOut.flush();
    }


    private static int[] constructNext(List<Integer> a) {
        int N = a.size();
        int R = 256;   // extend ASCII alphabet size
        int[] next = new int[N];

        int[] count = new int[R + 1];
        for (int i = 0; i < N; i++) {
            count[a.get(i) + 1]++;
        }
        for (int r = 0; r < R; r++) {
            count[r + 1] += count[r];
        }
        for (int i = 0; i < N; i++) {
            next[count[a.get(i)]++] = i;
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
