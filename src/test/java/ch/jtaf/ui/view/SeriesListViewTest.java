package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class SeriesListViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of(Role.ADMIN));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();
    }

    @Test
    void add_series() {
        // Add new series
        Button addSeries = _get(Button.class, spec -> spec.withId("add-series"));
        addSeries.click();

        TextField textField = _get(TextField.class);
        assertThat(textField.getValue()).isEmpty();

        textField.setValue("Test");

        Button save = _get(Button.class, spec -> spec.withId("save-series"));
        save.click();

        assertThat(_get(Notification.class).getElement().getOuterHTML()).isEqualTo("""
            <vaadin-notification suppress-template-warning>
             <template>
              Series saved
             </template>
            </vaadin-notification>""");

        UI.getCurrent().navigate(SeriesListView.class);

        // Check if series was added
        Grid<SeriesRecord> seriesGrid = _get(Grid.class, spec -> spec.withId("series-grid"));
        assertThat(GridKt._size(seriesGrid)).isEqualTo(3);
        assertThat(GridKt._get(seriesGrid, 2).getName()).isEqualTo("Test");

        // Remove series
        GridKt._getCellComponent(seriesGrid, 2, "delete-column").getChildren()
            .filter(component -> component instanceof Button).findFirst().map(component -> (Button) component)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();
        _get(Button.class, spec -> spec.withId("delete-series-confirm-dialog-confirm")).click();

        // Check if series was removed
        assertThat(GridKt._size(seriesGrid)).isEqualTo(2);
    }

}
