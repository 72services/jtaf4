package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class AthletesViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();

        UI.getCurrent().navigate(AthletesView.class);
    }

    @Test
    void check_athletes() {
        Grid<AthleteRecord> athletesGrid = _get(Grid.class, spec -> spec.withId("athletes-grid"));
        assertThat(GridKt._size(athletesGrid)).isEqualTo(140);

        AthleteRecord athleteRecord = GridKt._get(athletesGrid, 0);
        assertThat(athleteRecord.getLastName()).isEqualTo("Bangerter");
    }
}
