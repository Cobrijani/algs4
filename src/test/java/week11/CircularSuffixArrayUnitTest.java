package week11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CircularSuffixArrayUnitTest {


    @Test
    void circularTest() {
        CircularSuffixArray array = new CircularSuffixArray("ABRACADABRA!");

        int[] ints = new int[]{11, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2};

        for (int i = 0; i < ints.length; i++) {
            assertThat(array.index(i))
                    .isEqualTo(ints[i]);
        }

        assertThat(array.index(0))
                .isEqualTo(11);
    }

    @Test
    void wrongIndex() {
        CircularSuffixArray array = new CircularSuffixArray("ABRACADABRA!");


        assertThatThrownBy(() -> array.index(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
