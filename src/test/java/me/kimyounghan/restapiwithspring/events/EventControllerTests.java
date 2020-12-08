package me.kimyounghan.restapiwithspring.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest // MockMvc 빈을 자동 설정 해준다. 웹 관련 빈만 등록해준다.
public class EventControllerTests {

    @Autowired
    // MockMvc : 스프링 MVC 테스트 핵심 클래스
    // 웹 서버를 띄우지 않고도 스프링 MVC (DispatcherServlet) 가 요청을 처리하는 과정을 확인할 수 있기 때문에 컨트롤러 테스트용으로 자주 쓰임.
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // WebMvcTest 어노테이션으로 인해 웹 관련 빈만 등록되기 때문에 MockBean으로 리퍼지토리 주
    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
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
        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
        ;
    }

}
