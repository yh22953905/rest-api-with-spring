package me.kimyounghan.restapiwithspring.configs;

import me.kimyounghan.restapiwithspring.accounts.Account;
import me.kimyounghan.restapiwithspring.accounts.AccountRole;
import me.kimyounghan.restapiwithspring.accounts.AccountService;
import me.kimyounghan.restapiwithspring.common.CommonControllerTest;
import me.kimyounghan.restapiwithspring.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AuthServerConfigTest extends CommonControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급받는 테스트")
    public void getAuthToken() throws Exception {
        String username = "younghan@email.com";
        String password = "pass";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        mockMvc.perform(
                post("/oauth/token")
                        .with(httpBasic(clientId, clientSecret))
                        .param("username", username)
                        .param("password", password)
                        .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(jsonPath("access_token").exists());
    }

}