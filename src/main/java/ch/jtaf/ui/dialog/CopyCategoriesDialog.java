package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.service.SeriesService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.shared.Registration;
import org.jooq.DSLContext;

import static ch.jtaf.context.ApplicationContextHolder.getBean;
import static ch.jtaf.db.tables.Series.SERIES;

public class CopyCategoriesDialog extends Dialog {

    public CopyCategoriesDialog(long organizationId, long currentSeriesId) {
        var dsl = getBean(DSLContext.class);

        setHeaderTitle(getTranslation("Copy.Categories"));

        var close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());
        getHeader().add(close);

        var seriesSelection = new ComboBox<SeriesRecord>(getTranslation("Select.series.to.copy"));
        seriesSelection.setId("series-selection");
        seriesSelection.setWidth("300px");
        seriesSelection.setItemLabelGenerator(SeriesRecord::getName);
        seriesSelection.setItems(query -> dsl
            .selectFrom(SERIES)
            .where(SERIES.ORGANIZATION_ID.eq(organizationId))
            .and(SERIES.ID.ne(currentSeriesId))
            .orderBy(SERIES.NAME)
            .offset(query.getOffset()).limit(query.getLimit())
            .fetch().stream());

        add(seriesSelection);

        var copy = new Button(getTranslation("Copy"));
        copy.setId("copy-categories-copy");
        copy.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        copy.addClickListener(event -> {
            getBean(SeriesService.class).copyCategories(seriesSelection.getValue().getId(), currentSeriesId);
            Notification.show(getTranslation("Categories.copied"), 6000, Notification.Position.TOP_END);

            fireEvent(new AfterCopyEvent(this));
            close();
        });

        var cancel = new Button(getTranslation("Cancel"));
        cancel.addClickListener(event -> close());

        getFooter().add(copy, cancel);
    }

    public Registration addAfterCopyListener(ComponentEventListener<AfterCopyEvent> listener) {
        return addListener(AfterCopyEvent.class, listener);
    }

    public static class AfterCopyEvent extends ComponentEvent<CopyCategoriesDialog> {

        public AfterCopyEvent(CopyCategoriesDialog source) {
            super(source, false);
        }
    }
}
