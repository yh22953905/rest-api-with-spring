package me.kimyounghan.restapiwithspring.events;

import lombok.*;

import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
// @Data : EqualsAndHashCode 를 모든 필드를 다 써서 구현하기 때문에, 적어도 @Entity 에다가 @Data 를 쓰면 안됨. 상호 참조로 인해 StackOverflow 가 발생할 수 있음.
public class Event {

    private Integer id;
    private String name; private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional)
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    private EventStatus eventStatus = EventStatus.DRAFT;

}
