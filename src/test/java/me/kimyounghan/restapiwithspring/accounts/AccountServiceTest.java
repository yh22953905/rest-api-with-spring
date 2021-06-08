package me.kimyounghan.restapiwithspring.accounts;

import me.kimyounghan.restapiwithspring.common.CommonTest;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountServiceTest extends CommonTest {

//    @Rule // JUnit5 에서 사라짐
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        // Given
        String username = "younghan3@email.com";
        String password = "password";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        accountService.saveAccount(account);

        // When
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

//    @Test(expected = UsernameNotFoundException.class) // 발생할 예외의 타입을 확인할 수 있음.
    @Test
    public void findByUsernameFail() {
        String username = "notFoundUsername";

        try {
            accountService.loadUserByUsername(username);
            fail("supposed to be failed");
        } catch (UsernameNotFoundException e) {
//            assertThat(e instanceof UsernameNotFoundException).isTrue();
            assertThat(e.getMessage()).containsSequence(username);
        }
    }

    @Test
    public void findByUsernameFail2() {
//        // Expected
//        String username = "notFoundUsername";
//        expectedException.expect(UsernameNotFoundException.class); // JUnit5 에서 사라짐
//        expectedException.expectMessage(Matchers.containsString(username));
//
//        // When
//        accountService.loadUserByUsername(username);

        assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername("notFoundUsername"));
    }

}