package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
import static org.assertj.core.api.Assertions.assertThat;

class SeriesListViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();
    }

    @Test
    void add_series() {
        Button addSeries = _get(Button.class, spec -> spec.withId("add-series"));
        addSeries.click();

        TextField textField = _get(TextField.class);
        assertThat(textField.getValue()).isEmpty();

        textField.setValue("Test");

        Button save = _get(Button.class, spec -> spec.withId("save-series"));
        save.click();

        Notification notification = _get(Notification.class);
        assertThat(notification.getElement().getOuterHTML()).isEqualTo("""
            <vaadin-notification suppress-template-warning>
             <template>
              Series saved
             </template>
            </vaadin-notification>""");

        UI.getCurrent().navigate(SeriesListView.class);

        Grid<SeriesRecord> seriesGrid = _get(Grid.class, spec -> spec.withId("series-grid"));
        assertThat(GridKt._size(seriesGrid)).isEqualTo(3);

        SeriesRecord seriesRecord = GridKt._get(seriesGrid, 2);
        assertThat(seriesRecord.getName()).isEqualTo("Test");

        HorizontalLayout edit = (HorizontalLayout) GridKt._getCellComponent(seriesGrid, 2, "Delete");
        Button delete = (Button) edit.getChildren().filter(component -> component instanceof Button).findFirst().get();
        delete.click();

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();

        _fireConfirm(confirmDialog);

        assertThat(GridKt._size(seriesGrid)).isEqualTo(2);
    }

}
