package ch.jtaf.service;

import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Series.SERIES;

@Service
public class SeriesService {

    private final DSLContext dsl;

    public SeriesService(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public void deleteSeries(long seriesId) {
        dsl.deleteFrom(CATEGORY_EVENT)
            .where(CATEGORY_EVENT.category().SERIES_ID.eq(seriesId))
            .execute();

        dsl.deleteFrom(CATEGORY)
            .where(CATEGORY.SERIES_ID.eq(seriesId))
            .execute();

        dsl.deleteFrom(SERIES)
            .where(SERIES.ID.eq(seriesId))
            .execute();
    }
}
