package ch.jtaf.ui.view;

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

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class SeriesListViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();
    }

    @Test
    void select_organization_and_ensure_navigation_to_series_list() {
        UI.getCurrent().navigate(OrganizationsView.class);

        H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));
        assertThat(h1.getText()).isEqualTo("Organizations");

        Grid<OrganizationRecord> organizationsGrid = _get(Grid.class, spec -> spec.withId("organizations-grid"));
        assertThat(GridKt._size(organizationsGrid)).isEqualTo(2);

        HorizontalLayout edit = (HorizontalLayout) GridKt._getCellComponent(organizationsGrid, 0, "Edit");
        Button select = (Button) edit.getChildren().filter(component -> component instanceof Button).findFirst().get();
        select.click();

        h1 = _get(H1.class, spec -> spec.withId("view-title"));
        assertThat(h1.getText()).isEqualTo("Series");

        Grid<SeriesRecord> seriesGrid = _get(Grid.class, spec -> spec.withId("series-grid"));
        assertThat(GridKt._size(seriesGrid)).isEqualTo(2);

        SeriesRecord seriesRecord = GridKt._get(seriesGrid, 0);
        assertThat(seriesRecord.getName()).isEqualTo("CIS 2018");
    }

}
