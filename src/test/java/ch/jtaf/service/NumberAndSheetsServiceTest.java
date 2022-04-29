package ch.jtaf.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.Club.CLUB;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NumberAndSheetsServiceTest {

    @Autowired
    private NumberAndSheetsService numberAndSheetsService;

    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void create_numbers() {
        byte[] pdf = numberAndSheetsService.createNumbers(1L);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void create_numbers_ordered_by_club() {
        byte[] pdf = numberAndSheetsService.createNumbers(1L, CLUB.ABBREVIATION, CATEGORY.ABBREVIATION, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void create_empty_sheets() {
        byte[] pdf = numberAndSheetsService.createEmptySheets(1L, 1L);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void create_sheets() {
        byte[] pdf = numberAndSheetsService.createSheets(1L, 6L);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void create_sheets_ordered_by_club() {
        byte[] pdf = numberAndSheetsService.createSheets(1L, 6L, CLUB.ABBREVIATION, CATEGORY.ABBREVIATION, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME);

        assertThat(pdf).isNotEmpty();
    }

}
