package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.security.Role;
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

class EventsViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of(Role.ADMIN));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();

        UI.getCurrent().navigate(EventsView.class);
    }

    @Test
    void add_event() {
        // Check content of events grid
        Grid<EventRecord> eventsGrid = _get(Grid.class, spec -> spec.withId("events-grid"));
        assertThat(GridKt._size(eventsGrid)).isEqualTo(17);
        assertThat(GridKt._get(eventsGrid, 0).getName()).isEqualTo("60 m");

        // Add event
        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(Dialog.class, 1);

        _get(TextField.class, spec -> spec.withCaption("Abbreviation")).setValue("10");
        _get(TextField.class, spec -> spec.withCaption("Name")).setValue("Test");
        _get(Select.class, spec -> spec.withCaption("Gender")).setValue("F");
        _get(Select.class, spec -> spec.withCaption("Event Type")).setValue("RUN");
        _get(TextField.class, spec -> spec.withCaption("A")).setValue("1");
        _get(TextField.class, spec -> spec.withCaption("B")).setValue("1");
        _get(TextField.class, spec -> spec.withCaption("C")).setValue("1");
        _get(Button.class, spec -> spec.withCaption("Save")).click();

        // Check if event was added
        assertThat(GridKt._size(eventsGrid)).isEqualTo(18);
        assertThat(GridKt._get(eventsGrid, 0).getName()).isEqualTo("Test");

        // Remove event
        GridKt._getCellComponent(eventsGrid, 0, "edit-column").getChildren()
            .filter(component -> component instanceof Button).findFirst().map(component -> (Button) component)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();
        _fireConfirm(confirmDialog);

        // Check if event was removed
        assertThat(GridKt._size(eventsGrid)).isEqualTo(17);
    }
}
