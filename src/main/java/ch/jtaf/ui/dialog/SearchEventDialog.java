package ch.jtaf.ui.dialog;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.EventRecord;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.Serial;
import java.util.function.Consumer;

import static ch.jtaf.context.ApplicationContextHolder.getBean;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Event.EVENT;
import static org.jooq.impl.DSL.upper;

public class SearchEventDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FULLSCREEN = "fullscreen";

    private boolean isFullScreen = false;
    private final Div content;
    private final Button toggle;

    private final ConfigurableFilterDataProvider<EventRecord, Void, String> dataProvider;

    public SearchEventDialog(DSLContext dsl, CategoryRecord categoryRecord, Consumer<EventRecord> onSelect) {
        setId("search-event-dialog");

        getElement().getThemeList().add("jtaf-dialog");
        getElement().setAttribute("aria-labelledby", "dialog-title");

        setDraggable(true);
        setResizable(true);

        var headerTitel = new H2(getTranslation("Events"));
        headerTitel.addClassName("dialog-title");

        toggle = new Button(VaadinIcon.EXPAND_SQUARE.create());
        toggle.setId("search-event-dialog-toggle");
        toggle.addClickListener(event -> toggle());

        var close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        var header = new Header(headerTitel, toggle, close);
        header.getElement().getThemeList().add(Lumo.LIGHT);
        add(header);

        var filter = new TextField(getTranslation("Filter"));
        filter.setId("event-filter");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.focus();

        CallbackDataProvider<EventRecord, String> callbackDataProvider = DataProvider.fromFilteringCallbacks(
            query -> dsl
                .selectFrom(EVENT)
                .where(EVENT.ORGANIZATION_ID.eq(getBean(OrganizationProvider.class).getOrganization().getId()))
                .and(EVENT.GENDER.eq(categoryRecord.getGender()))
                .and(EVENT.ID.notIn(dsl
                    .select(CATEGORY_EVENT.EVENT_ID)
                    .from(CATEGORY_EVENT)
                    .where(CATEGORY_EVENT.CATEGORY_ID.eq(categoryRecord.getId()))
                ))
                .and(createCondition(query))
                .orderBy(EVENT.ABBREVIATION, EVENT.GENDER)
                .offset(query.getOffset()).limit(query.getLimit())
                .fetchStream(),
            query -> {
                var count = dsl
                    .selectCount()
                    .from(EVENT)
                    .where(EVENT.ORGANIZATION_ID.eq(getBean(OrganizationProvider.class).getOrganization().getId()))
                    .and(EVENT.GENDER.eq(categoryRecord.getGender()))
                    .and(EVENT.ID.notIn(dsl
                        .select(CATEGORY_EVENT.EVENT_ID)
                        .from(CATEGORY_EVENT)
                        .where(CATEGORY_EVENT.CATEGORY_ID.eq(categoryRecord.getId()))
                    ))
                    .and(createCondition(query))
                    .fetchOneInto(Integer.class);
                return count != null ? count : 0;
            });

        dataProvider = callbackDataProvider.withConfigurableFilter();

        var grid = new Grid<EventRecord>();
        grid.setId("events-grid");
        grid.setItems(dataProvider);
        grid.getStyle().set("height", "calc(100% - 300px");

        grid.addColumn(EventRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true).setKey(EVENT.ABBREVIATION.getName());
        grid.addColumn(EventRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setKey(EVENT.NAME.getName());
        grid.addColumn(EventRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true).setKey(EVENT.GENDER.getName());
        grid.addColumn(EventRecord::getEventType).setHeader(getTranslation("Event.Type")).setSortable(true).setKey(EVENT.EVENT_TYPE.getName());
        grid.addColumn(EventRecord::getA).setHeader("A");
        grid.addColumn(EventRecord::getA).setHeader("B");
        grid.addColumn(EventRecord::getA).setHeader("C");

        grid.addComponentColumn(eventRecord -> new Button(getTranslation("Assign.Event"), e -> {
            onSelect.accept(eventRecord);
            Notification.show(getTranslation("Event.assigned"), 6000, Notification.Position.TOP_END);
            dataProvider.refreshAll();
        })).setKey("assign-column");

        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        content = new Div(filter, grid);
        content.addClassName("dialog-content");
        add(content);

        toggle();

        filter.focus();
    }

    private void initialSize() {
        toggle.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("auto");
        setWidth("600px");
    }

    private void toggle() {
        if (isFullScreen) {
            initialSize();
        } else {
            toggle.setIcon(VaadinIcon.COMPRESS_SQUARE.create());
            getElement().getThemeList().add(FULLSCREEN);
            setSizeFull();
            content.setVisible(true);
        }
        isFullScreen = !isFullScreen;
    }

    private Condition createCondition(Query<?, ?> query) {
        var optionalFilter = query.getFilter();
        if (optionalFilter.isPresent()) {
            String filterString = (String) optionalFilter.get();
            if (StringUtils.isNumeric(filterString)) {
                return EVENT.ID.eq(Long.valueOf(filterString));
            } else {
                return upper(EVENT.ABBREVIATION).like(filterString.toUpperCase() + "%")
                    .or(upper(EVENT.NAME).like(filterString.toUpperCase() + "%"));
            }
        } else {
            return DSL.condition("1 = 1");
        }
    }

}
