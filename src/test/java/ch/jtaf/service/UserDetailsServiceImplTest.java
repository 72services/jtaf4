package ch.jtaf.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void load_user_by_username() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("simon@martinelli.ch");

        assertThat(userDetails.getUsername()).isEqualTo("simon@martinelli.ch");
    }

    @Test
    void load_unknown_user() {
        assertThatExceptionOfType(UsernameNotFoundException.class)
            .isThrownBy(() -> userDetailsService.loadUserByUsername("jane@doe.com"));
    }

}
