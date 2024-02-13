package ch.jtaf.service;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void create_user_and_confirm() {
        assertThatNoException().isThrownBy(() -> {
            SecurityUserRecord user = userService.createUser("Peter", "Muster", "peter.muster@nodomain.xyz", "pass", null);

            boolean confirmed = userService.confirm(user.getConfirmationId());

            assertThat(confirmed).isTrue();
        });
    }

    @Test
    void confirm_with_invalid_confirmation_id() {
        boolean confirmed = userService.confirm(UUID.randomUUID().toString());

        assertThat(confirmed).isFalse();
    }

    @Test
    void user_exist() {
        assertThatExceptionOfType(UserAlreadyExistException.class)
            .isThrownBy(() ->
                userService.createUser("Simon", "Martinelli", "simon@martinelli.ch", "pass", null));
    }
}
