package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.jtaf.ui.view.SeriesListViewTest.navigateToSeriesList;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class SeriesViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();
    }

    @Test
    void select_series() {
        Grid<SeriesRecord> seriesGrid = navigateToSeriesList();

        GridKt._clickItem(seriesGrid, 0);

        Grid<CompetitionRecord> competitionsGrid = _get(Grid.class, spec -> spec.withId("competitions-grid"));

        assertThat(GridKt._size(competitionsGrid)).isEqualTo(2);

        CompetitionRecord competitionRecord = GridKt._get(competitionsGrid, 0);
        assertThat(competitionRecord.getName()).isEqualTo("1. CIS Twann");
    }

}
