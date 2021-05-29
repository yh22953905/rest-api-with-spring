package me.kimyounghan.restapiwithspring.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import me.kimyounghan.restapiwithspring.accounts.Account;
import me.kimyounghan.restapiwithspring.accounts.AccountSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
// @Data : EqualsAndHashCode 를 모든 필드를 다 써서 구현하기 때문에, 적어도 @Entity 에다가 @Data 를 쓰면 안됨. 상호 참조로 인해 StackOverflow 가 발생할 수 있음.
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
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
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account user;

    public void update() {
        // Update free
        if (basePrice == 0 && maxPrice == 0) {
            free = true;
        } else {
            free = false;
        }

        // Update offline
        if (location == null || location.isBlank()) {
            offline = false;
        } else {
            offline = true;
        }
    }
}
