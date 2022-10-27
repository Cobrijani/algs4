package week11;

import java.util.ArrayList;
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


        int[] next = constructNext(codes);
        char[] result = new char[codes.size()];

        int nextIndex = first;
        for (int i = 0; i < next.length; i++) {
            nextIndex = next[nextIndex];
            result[i] = (char) codes.get(nextIndex).intValue();

        }
        return String.valueOf(result);
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


}
