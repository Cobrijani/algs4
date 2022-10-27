package week11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoveToFrontTest {

    private MoveToFrontTest() {
        throw new IllegalStateException();
    }

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
    public static List<Integer> encode(char[] input) {

        List<Integer> out = new ArrayList<>();
        int[] offsets = offset();

        for (char nextChar : input) {
            int nextIndex = nextChar + offsets[nextChar];
            out.add(nextIndex);

            for (int i = 0; i < offsets.length; i++) {
                int index = i + offsets[i];

                if (index < nextIndex) {
                    offsets[i] += 1;
                }
            }
            offsets[nextChar] = -nextChar;
        }

        return out;
    }

    public static String decode(List<Integer> integers) {
        StringBuilder builder = new StringBuilder();
        char[] chars = init();

        for (int next : integers) {

            char nextChar = chars[next];
            builder.append(nextChar);

            for (int i = next; i > 0; i--) {
                chars[i] = chars[i - 1];
            }
            chars[0] = nextChar;
        }

        return builder.toString();
    }


}
