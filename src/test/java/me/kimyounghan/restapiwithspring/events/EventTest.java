package me.kimyounghan.restapiwithspring.events;

import junitparams.JUnitParamsRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

// Domain test
@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Inflearn Spring REST API")
                .description("REST API development with Spring")
                .build()
        ;
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

//    @Test
//    @Parameters // parametersFor{메소드명} 이라는 prefix 로 따로 메소드명을 지정하지 않을 수 있다.
////    @Parameters(method = "parametersForTestFree")
    @ParameterizedTest
    @MethodSource("parametersForTestFree")
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build()
                ;

        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

//    private Object[] parametersForTestFree() {
//        return new Object[] {
//                new Object[] {0, 0, true},
//                new Object[] {100, 0, false},
//                new Object[] {0, 100, false},
//                new Object[] {100, 200, false}
//        };
//    }

    static Stream<Arguments> parametersForTestFree() {
        return Stream.of(
                Arguments.arguments(0, 0, true)
                , Arguments.arguments(100, 0, false)
                , Arguments.arguments(0, 100, false)
                , Arguments.arguments(100, 200, false)
        );
    }

//    @Test
//    @Parameters
    @ParameterizedTest
    @MethodSource("parametersForTestOffLine")
    public void testOffLine(String location, boolean isOffline) {
        // Given
        Event event = Event.builder()
                .location("강남역")
                .build()
        ;

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isTrue();
    }

//    private Object[] parametersForTestOffLine() {
//        return new Object[]{
//                new Object[] {"강남", true},
//                new Object[] {null, false}
//        };
//    }

    static Stream<Arguments> parametersForTestOffLine() {
        return Stream.of(
                Arguments.arguments("강남", true)
                , Arguments.arguments(null, false)
        );
    }

}