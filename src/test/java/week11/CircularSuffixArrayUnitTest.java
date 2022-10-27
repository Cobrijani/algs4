package week11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CircularSuffixArrayUnitTest {


    @Test
    void circularTest() {
        CircularSuffixArray array = new CircularSuffixArray("ABRACADABRA!");

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
