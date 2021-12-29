package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class SeriesViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();

        Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
        GridKt._clickItem(seriesGrid, 0);
    }

    @Test
    void check_series_name() {
        TextField name = _get(TextField.class);
        assertThat(name.getValue()).isEqualTo("CIS 2018");
    }

    @Test
    void check_competitions_tab() {
        Grid<CompetitionRecord> competitionsGrid = _get(Grid.class, spec -> spec.withId("competitions-grid"));
        assertThat(GridKt._size(competitionsGrid)).isEqualTo(2);

        CompetitionRecord competitionRecord = GridKt._get(competitionsGrid, 0);
        assertThat(competitionRecord.getName()).isEqualTo("1. CIS Twann");
    }

    @Test
    void check_categories_tab() {
        Tabs tabs = _get(Tabs.class);
        Tab categories = _get(Tab.class, spec -> spec.withText("Categories"));
        tabs.setSelectedTab(categories);

        Grid<CategoryRecord> categoriesGrid = _get(Grid.class, spec -> spec.withId("categories-grid"));
        assertThat(GridKt._size(categoriesGrid)).isEqualTo(12);

        CategoryRecord categoryRecord = GridKt._get(categoriesGrid, 0);
        assertThat(categoryRecord.getAbbreviation()).isEqualTo("A");
    }

    @Test
    void check_athletes_tab() {
        Tabs tabs = _get(Tabs.class);
        Tab athletes = _get(Tab.class, spec -> spec.withText("Athletes"));
        tabs.setSelectedTab(athletes);

        Grid<AthleteRecord> athletesGrid = _get(Grid.class, spec -> spec.withId("athletes-grid"));
        assertThat(GridKt._size(athletesGrid)).isEqualTo(85);

        AthleteRecord athleteRecord = GridKt._get(athletesGrid, 0);
        assertThat(athleteRecord.getLastName()).isEqualTo("Scholer");
    }

}
