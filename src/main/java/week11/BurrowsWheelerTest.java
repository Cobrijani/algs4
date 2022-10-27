package week11;

import edu.princeton.cs.algs4.LSD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BurrowsWheelerTest {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static List<Integer> transform(String next) {
        final List<Integer> out = new ArrayList<>();

        final CircularSuffixArray circularSuffixArray = new CircularSuffixArray(next);

        int first = -1;
        char[] firstRow = new char[next.length()];

        for (int i = 0; i < next.length(); i++) {
            int nextIndex = circularSuffixArray.index(i);

            if (nextIndex == 0) {
                first = i;
            }
            firstRow[i] = next
                    .charAt((next.length() - 1 + nextIndex) % next.length());
        }
        out.add(first);
        for (char item : firstRow) {
            out.add((int) item);
        }
        return out;
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static String inverseTransform(List<Integer> integers) {
        int first = integers.get(0);
        final List<Integer> codes = integers.subList(1, integers.size());
        int[] t = new int[codes.size()];
        for (int i = 0; i < codes.size(); i++) {
            t[i] = codes.get(i);
        }
        int[] sortedT = Arrays.copyOf(t, t.length);
        LSD.sort(sortedT);

        int[] next = constructNext(t, sortedT);
        char[] result = new char[t.length];

        int nextIndex = first;
        for (int i = 0; i < next.length; i++) {
            result[i] = (char) sortedT[nextIndex];
            nextIndex = next[nextIndex];
        }
        return String.valueOf(result);
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


}
