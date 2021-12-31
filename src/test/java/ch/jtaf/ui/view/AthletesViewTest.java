package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
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
    void add_athlete() {
        // Check content of athletes grid
        Grid<AthleteRecord> athletesGrid = _get(Grid.class, spec -> spec.withId("athletes-grid"));
        assertThat(GridKt._size(athletesGrid)).isEqualTo(140);
        assertThat(GridKt._get(athletesGrid, 0).getLastName()).isEqualTo("Bangerter");

        // Add a new athlete
        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(Dialog.class, 1);

        _get(TextField.class, spec -> spec.withCaption("Last Name")).setValue("Test");
        _get(TextField.class, spec -> spec.withCaption("First Name")).setValue("Test");
        _get(Select.class, spec -> spec.withCaption("Gender")).setValue("F");
        _get(TextField.class, spec -> spec.withCaption("Year")).setValue("2000");
        _get(Button.class, spec -> spec.withCaption("Save")).click();

        // Check if athlete was added
        assertThat(GridKt._size(athletesGrid)).isEqualTo(141);
        assertThat(GridKt._get(athletesGrid, 0).getLastName()).isEqualTo("Test");

        // Remove athlete
        GridKt._getCellComponent(athletesGrid, 0, "edit-column").getChildren()
            .filter(component -> component instanceof Button).findFirst().map(component -> (Button) component)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();
        _fireConfirm(confirmDialog);

        // Check that athlete was removed
        assertThat(GridKt._size(athletesGrid)).isEqualTo(140);
    }

    @Test
    void filter_and_sort() {
        // Check number of athletes before filtering
        Grid<AthleteRecord> athletesGrid = _get(Grid.class, spec -> spec.withId("athletes-grid"));
        assertThat(GridKt._size(athletesGrid)).isEqualTo(140);

        // Filter
        _get(TextField.class, spec -> spec.withId("filter")).setValue("Martinelli");
        assertThat(GridKt._size(athletesGrid)).isEqualTo(1);

        // Remove filter
        _get(TextField.class, spec -> spec.withId("filter")).setValue("");
        assertThat(GridKt._size(athletesGrid)).isEqualTo(140);

        // Sort grid
        // TODO https://github.com/mvysny/karibu-testing/issues/97
        // GridKt.sort(athletesGrid, new QuerySortOrder("Last Name", SortDirection.ASCENDING));
    }
}
