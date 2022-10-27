package week11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MoveToFrontUnitTest {

    private static final List<Integer> EXPECTED =
            List.of(65, 66, 82, 2, 68, 1, 69, 1, 4, 4, 2, 38);


    @ParameterizedTest
    @MethodSource
    void encode(char[] input, List<Integer> expected) {
        List<Integer> ret = MoveToFrontTest
                .encode(input);

        assertThat(ret)
                .asList()
                .isEqualTo(expected);


    }

    private static Stream<Arguments> encode() {
        return Stream.of(
                Arguments.of(new char[]{'A', 'B', 'R', 'A', 'C', 'A', 'D', 'A', 'B', 'R', 'A', '!'}, EXPECTED)
        );
    }

    @Test
    void decode() {
        String ret = MoveToFrontTest.decode(EXPECTED);
        assertThat(ret).isEqualTo("ABRACADABRA!");
    }
}
