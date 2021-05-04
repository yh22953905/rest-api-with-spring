package me.kimyounghan.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kimyounghan.restapiwithspring.common.RestDocsConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringRunner.class) JUnit5로 변경
//@WebMvcTest // AutoConfigureMockMvc 어노테이션이 있어 MockMvc 빈을 자동 설정 해준다. 웹 관련 빈만 등록해준다.
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {

    @Autowired
    // MockMvc : 스프링 MVC 테스트 핵심 클래스
    // 웹 서버를 띄우지 않고도 스프링 MVC (DispatcherServlet) 가 요청을 처리하는 과정을 확인할 수 있기 때문에 컨트롤러 테스트용으로 자주 쓰임.
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // WebMvcTest 어노테이션으로 인해 웹 관련 빈만 등록되기 때문에 MockBean으로 리퍼지토리 주입
//    @MockBean
//    EventRepository eventRepository;

    @Test
//    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 12, 8, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 12, 9, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2020, 12, 10, 0, 0))
                .endEventDateTime(LocalDateTime.of(2020, 12, 11, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event"))
        ;
    }

    @Test
//    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    @DisplayName("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 12, 8, 0, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 12, 9, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2020, 12, 10, 0, 0))
                .endEventDateTime(LocalDateTime.of(2020, 12, 11, 0, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();
//        Mockito.when(eventRepository.save(event)).thenReturn(event); // EventDto로 파라미터를 받으면 Mock 객체에서 save 할 때 쓰는 event 객체가 위에서 build 한 객체와 다르므로 NullPointerException 발

        mockMvc.perform(post("/api/events/")
//                .contentType(MediaType.APPLICATION_JSON_UTF8) // deprecated
//                스프링 5.2 부터는 UTF-8 이 기본 Charset
//                스프링 부트 2.2.0 업그레이드 후 응답 Content-Type 에서 Charset 빠짐
//                출처 : http://honeymon.io/tech/2019/10/23/spring-deprecated-media-type.html
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
//    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    @DisplayName("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = new EventDto().builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
//    @TestDescription("입력값이 잘못 경우에 에러가 발생하는 테스트")
    @DisplayName("입력값이 잘못 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 12, 26, 0, 0)) // 등록 시작 날짜가 등록 종료 날짜보다 이름.
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 12, 25, 0, 0))
                .beginEventDateTime(LocalDateTime.of(2020, 12, 24, 0, 0))
                .endEventDateTime(LocalDateTime.of(2020, 12, 23, 0, 0))
                .basePrice(100)
                .maxPrice(50)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }

}
