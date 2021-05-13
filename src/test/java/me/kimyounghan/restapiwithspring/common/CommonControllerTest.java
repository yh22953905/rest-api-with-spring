package me.kimyounghan.restapiwithspring.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

//@RunWith(SpringRunner.class) JUnit5로 변경
//@WebMvcTest // AutoConfigureMockMvc 어노테이션이 있어 MockMvc 빈을 자동 설정 해준다. 웹 관련 빈만 등록해준다.
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore
public class CommonControllerTest {

    @Autowired
    // MockMvc : 스프링 MVC 테스트 핵심 클래스
    // 웹 서버를 띄우지 않고도 스프링 MVC (DispatcherServlet) 가 요청을 처리하는 과정을 확인할 수 있기 때문에 컨트롤러 테스트용으로 자주 쓰임.
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

}
