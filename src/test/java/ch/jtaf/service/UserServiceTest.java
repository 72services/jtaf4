package ch.jtaf.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private JavaMailSender javaMailSender;


    @Test
    void create_user_and_send_confirmation_email() {
        userService.createUserAndSendConfirmationEmail("Peter", "Muster", "peter.muster@nodomain.xyz", "pass", Locale.ENGLISH);
    }

    @Test
    void confirm_with_invalid_confirmation_id() {
        boolean confirmed = userService.confirm(UUID.randomUUID().toString());

        assertThat(confirmed).isFalse();
    }
}
