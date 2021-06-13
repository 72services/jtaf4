package ch.jtaf.db;

import ch.jtaf.db.tables.records.AthleteRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JooqTest {

    @Autowired
    private DSLContext dsl;

    @Test
    void join() {
        List<AthleteRecord> result = dsl
            .select()
            .from(ATHLETE)
            .join(CLUB).on(CLUB.ID.eq(ATHLETE.ID))
            .fetchInto(AthleteRecord.class);

        assertThat(result.size()).isEqualTo(4);
    }
}
