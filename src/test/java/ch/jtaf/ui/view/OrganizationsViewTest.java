package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
import static org.assertj.core.api.Assertions.assertThat;

class OrganizationsViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));

        UI.getCurrent().navigate(OrganizationsView.class);
    }

    @Test
    void add_organization() {
        H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));
        assertThat(h1.getText()).isEqualTo("Organizations");

        Grid<OrganizationRecord> organizationGrid = _get(Grid.class, spec -> spec.withId("organizations-grid"));
        assertThat(GridKt._size(organizationGrid)).isEqualTo(2);

        OrganizationRecord organizationRecord = GridKt._get(organizationGrid, 0);
        assertThat(organizationRecord.getOrganizationKey()).isEqualTo("CIS");

        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(Dialog.class, 1);

        _get(TextField.class, spec -> spec.withCaption("Key")).setValue("AAA");
        _get(TextField.class, spec -> spec.withCaption("Name")).setValue("Test");
        _get(Button.class, spec -> spec.withCaption("Save")).click();

        assertThat(GridKt._size(organizationGrid)).isEqualTo(3);

        organizationRecord = GridKt._get(organizationGrid, 2);
        assertThat(organizationRecord.getOrganizationKey()).isEqualTo("AAA");

        HorizontalLayout edit = (HorizontalLayout) GridKt._getCellComponent(organizationGrid, 2, "Edit");
        Button delete = (Button) edit.getChildren().filter(component -> component instanceof Button && ((Button) component).getText().equals("Delete")).findFirst().get();
        delete.click();

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();

        _fireConfirm(confirmDialog);

        assertThat(GridKt._size(organizationGrid)).isEqualTo(2);
    }
}
