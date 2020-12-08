package me.kimyounghan.restapiwithspring.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data  @Builder @NoArgsConstructor @AllArgsConstructor
// 파라미터로 넘어오는 값 중 id 등의 넘어오지 않아도 되는 값은 제외하고, @Validated 등의 어노테이션이 Event 클래스에 너무 많아지는 것을 막기 위해 별도로 생성한 Dto 클래스
// Event 클래스와 일부 필드가 중복되는 단점이 있다.
public class EventDto {

    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이 값이 없으면 온라인 모
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;

}
