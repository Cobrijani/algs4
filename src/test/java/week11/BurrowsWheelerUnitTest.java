package week11;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BurrowsWheelerUnitTest {

    public static final String EXPECTED_TEXT = "ABRACADABRA!";
    public static final List<Integer> EXPECTED_TRANSFORM = List.of(3, 65, 82, 68, 33, 82, 67, 65, 65, 65, 65, 66, 66);

    @ParameterizedTest
    @MethodSource
    void transform(String text, List<Integer> transform) {

        List<Integer> result = BurrowsWheelerTest.transform(text);

        assertThat(result)
                .asList()
                .isEqualTo(transform);

    }


    @ParameterizedTest
    @MethodSource
    void inverseTransform(String text, List<Integer> transform) {
        String actual = BurrowsWheelerTest.inverseTransform(transform);
        assertThat(actual)
                .isEqualTo(text);
    }

    private static Stream<Arguments> transform() {
        return Stream.of(
                Arguments.of(EXPECTED_TEXT, EXPECTED_TRANSFORM),
                Arguments.of("ZEBRA", List.of(4, 82, 69, 90, 66, 65)),
                Arguments.of("AMENDMENTS", List.of(0, 83, 78, 77, 77, 65, 68, 69, 69, 84, 78))
        );
    }

    private static Stream<Arguments> inverseTransform() {
        return transform();
    }
}
