import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class MoveToFront {


    private static char[] init() {
        char[] r = new char[256];
        for (int i = 0; i < r.length; i++) {
            r[i] = (char) i;
        }
        return r;
    }

    private static int[] offset() {
        int[] r = new int[256];
        Arrays.fill(r, 0);
        return r;
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] offsets = offset();

        while (!BinaryStdIn.isEmpty()) {
            char nextChar = BinaryStdIn.readChar(8);
            int nextIndex = nextChar + offsets[nextChar];
            BinaryStdOut.write(nextIndex, 8);

            for (int i = 0; i < offsets.length; i++) {
                int index = i + offsets[i];

                if (index < nextIndex) {
                    offsets[i] += 1;
                }
            }
            offsets[nextChar] = -nextChar;
        }

        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] chars = init();

        while (!BinaryStdIn.isEmpty()) {
            int next = BinaryStdIn.readInt(8);
            char nextChar = chars[next];
            BinaryStdOut.write(nextChar, 8);
            for (int i = next; i > 0; i--) {
                chars[i] = chars[i - 1];
            }
            chars[0] = nextChar;
        }

        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if ("-".equals(args[0])) {
            MoveToFront.encode();
        }
        if ("+".equals(args[0])) {
            MoveToFront.decode();
        }
    }
}
