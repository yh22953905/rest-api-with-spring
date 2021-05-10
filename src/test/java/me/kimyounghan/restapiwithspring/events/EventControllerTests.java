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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringRunner.class) JUnit5로 변경
//@WebMvcTest // AutoConfigureMockMvc 어노테이션이 있어 MockMvc 빈을 자동 설정 해준다. 웹 관련 빈만 등록해준다.
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
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

    @Autowired
    EventRepository eventRepository;

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

                // 하위 응답에서 테스트하기 때문에 필요없음.
//                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.query-events").exists())
//                .andExpect(jsonPath("_links.update-event").exists())
//                .andExpect(jsonPath("_links.profile").exists())
                .andDo(
                        document(
                                "create-event"
                                , links(
                                        linkWithRel("self").description("link to self")
                                        , linkWithRel("query-events").description("link to query events")
                                        , linkWithRel("update-event").description("link to update an existing event")
                                        , linkWithRel("profile").description("link to profile")
                                )
                                , requestHeaders(
                                        headerWithName(HttpHeaders.ACCEPT).description("accept header")
                                        , headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                                )
                                , requestFields(
                                        fieldWithPath("name").description("name of new event")
                                        , fieldWithPath("description").description("description of new event")
                                        , fieldWithPath("beginEnrollmentDateTime").description("date time of beginning enrollment of new event")
                                        , fieldWithPath("closeEnrollmentDateTime").description("date time of closing enrollment of new event")
                                        , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                        , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                        , fieldWithPath("location").description("location of new event")
                                        , fieldWithPath("basePrice").description("base price of new event")
                                        , fieldWithPath("maxPrice").description("max price of new event")
                                        , fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                                )
                                , responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("location header")
                                        , headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                                )
                                , responseFields(
                                        fieldWithPath("id").description("identifier of new event")
                                        , fieldWithPath("name").description("name of new event")
                                        , fieldWithPath("description").description("description of new event")
                                        , fieldWithPath("beginEnrollmentDateTime").description("date time of beginning enrollment of new event")
                                        , fieldWithPath("closeEnrollmentDateTime").description("date time of closing enrollment of new event")
                                        , fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                        , fieldWithPath("endEventDateTime").description("date time of end of new event")
                                        , fieldWithPath("location").description("location of new event")
                                        , fieldWithPath("basePrice").description("base price of new event")
                                        , fieldWithPath("maxPrice").description("max price of new event")
                                        , fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                                        , fieldWithPath("free").description("it tells if this event is free or not")
                                        , fieldWithPath("offline").description("it tells if this event is offline event of not")
                                        , fieldWithPath("eventStatus").description("event status")
                                        , fieldWithPath("_links.self.href").description("link to self")
                                        , fieldWithPath("_links.query-events.href").description("link to query event")
                                        , fieldWithPath("_links.update-event.href").description("link to update existing event")
                                        , fieldWithPath("_links.profile.href").description("link to profile")
                                )
                        )
                )
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
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두 번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,desc")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-events"))
        ;
    }

    private void generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("test event")
                .build();

        eventRepository.save(event);
    }

}
