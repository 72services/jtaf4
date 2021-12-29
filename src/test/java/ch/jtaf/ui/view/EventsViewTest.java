package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class EventsViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();

        UI.getCurrent().navigate(EventsView.class);
    }

    @Test
    void check_events() {
        Grid<EventRecord> eventsGrid = _get(Grid.class, spec -> spec.withId("events-grid"));
        assertThat(GridKt._size(eventsGrid)).isEqualTo(17);

        EventRecord eventRecord = GridKt._get(eventsGrid, 0);
        assertThat(eventRecord.getName()).isEqualTo("60 m");
    }
}
