package ch.jtaf.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NumberAndSheetsServiceTest {

    @Autowired
    private NumberAndSheetsService numberAndSheetsService;

    @MockBean
    private UserService userService;


    @Test
    void create_numbers() {
        byte[] pdf = numberAndSheetsService.createNumbers(1L);

        assertThat(pdf.length).isGreaterThan(0);
    }

    @Test
    void create_empty_sheets() {
        byte[] pdf = numberAndSheetsService.createEmptySheets(1L, 1L);

        assertThat(pdf.length).isGreaterThan(0);
    }

    @Test
    void create_sheets() {
        byte[] pdf = numberAndSheetsService.createSheets(1L, 6L);

        assertThat(pdf.length).isGreaterThan(0);
    }
}
