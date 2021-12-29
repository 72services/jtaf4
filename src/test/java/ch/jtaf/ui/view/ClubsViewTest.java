package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class ClubsViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();

        UI.getCurrent().navigate(ClubsView.class);
    }

    @Test
    void check_clubs() {
        Grid<ClubRecord> clubsGrid = _get(Grid.class, spec -> spec.withId("clubs-grid"));
        assertThat(GridKt._size(clubsGrid)).isEqualTo(4);

        ClubRecord clubRecord = GridKt._get(clubsGrid, 0);
        assertThat(clubRecord.getName()).isEqualTo("Erlach");
    }
}
