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
    public void copyCategories(Long seriesIdToCopy, Long currentSeriesId) {
        dsl.selectFrom(CATEGORY)
            .where(CATEGORY.SERIES_ID.eq(seriesIdToCopy))
            .fetch()
            .forEach(category -> {
                var copyCategory = category.copy();
                copyCategory.setSeriesId(currentSeriesId);
                dsl.attach(copyCategory);
                copyCategory.store();
                dsl.selectFrom(CATEGORY_EVENT)
                    .where(CATEGORY_EVENT.CATEGORY_ID.eq(category.getId()))
                    .fetch()
                    .forEach(categoryEvent -> {
                        var copyCategoryEvent = categoryEvent.copy();
                        copyCategoryEvent.setCategoryId(copyCategory.getId());
                        copyCategoryEvent.setEventId(categoryEvent.getEventId());
                        dsl.attach(copyCategoryEvent);
                        copyCategoryEvent.store();
                    });
            });
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
