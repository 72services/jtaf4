package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.KaribuTest;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.pro.ConfirmDialogKt._fireConfirm;
import static org.assertj.core.api.Assertions.assertThat;

class SeriesViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();

        Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
        GridKt._clickItem(seriesGrid, 0);

        TextField name = _get(TextField.class);
        assertThat(name.getValue()).isEqualTo("CIS 2018");
    }

    @Test
    void add_competition() {
        Grid<CompetitionRecord> competitionsGrid = _get(Grid.class, spec -> spec.withId("competitions-grid"));
        assertThat(GridKt._size(competitionsGrid)).isEqualTo(2);

        CompetitionRecord competitionRecord = GridKt._get(competitionsGrid, 0);
        assertThat(competitionRecord.getName()).isEqualTo("1. CIS Twann");

        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(Dialog.class, 1);

        _get(TextField.class, spec -> spec.withCaption("Name").withValue("")).setValue("Test");
        _get(DatePicker.class, spec -> spec.withCaption("Date")).setValue(LocalDate.now());
        _get(Button.class, spec -> spec.withId("edit-save")).click();

        assertThat(GridKt._size(competitionsGrid)).isEqualTo(3);

        competitionRecord = GridKt._get(competitionsGrid, 2);
        assertThat(competitionRecord.getName()).isEqualTo("Test");

        HorizontalLayout edit = (HorizontalLayout) GridKt._getCellComponent(competitionsGrid, 2, "Edit");
        Button delete = (Button) edit.getChildren().filter(component -> component instanceof Button).findFirst().get();
        delete.click();

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();

        _fireConfirm(confirmDialog);

        assertThat(GridKt._size(competitionsGrid)).isEqualTo(2);
    }

    @Test
    void add_category() {
        Tabs tabs = _get(Tabs.class);
        Tab categories = _get(Tab.class, spec -> spec.withText("Categories"));
        tabs.setSelectedTab(categories);

        Grid<CategoryRecord> categoriesGrid = _get(Grid.class, spec -> spec.withId("categories-grid"));
        assertThat(GridKt._size(categoriesGrid)).isEqualTo(12);

        CategoryRecord categoryRecord = GridKt._get(categoriesGrid, 0);
        assertThat(categoryRecord.getAbbreviation()).isEqualTo("A");

        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(Dialog.class, 1);

        _get(TextField.class, spec -> spec.withCaption("Abbreviation").withValue("")).setValue("1");
        _get(TextField.class, spec -> spec.withCaption("Name").withValue("")).setValue("Test");
        _get(Select.class, spec -> spec.withCaption("Gender")).setValue("M");
        _get(TextField.class, spec -> spec.withCaption("Year from")).setValue("1999");
        _get(TextField.class, spec -> spec.withCaption("Year to")).setValue("2000");
        _get(Button.class, spec -> spec.withId("edit-save")).click();

        assertThat(GridKt._size(categoriesGrid)).isEqualTo(13);

        categoryRecord = GridKt._get(categoriesGrid, 0);
        assertThat(categoryRecord.getAbbreviation()).isEqualTo("1");

        HorizontalLayout edit = (HorizontalLayout) GridKt._getCellComponent(categoriesGrid, 0, "Edit");
        Button delete = (Button) edit.getChildren().filter(component -> component instanceof Button).findFirst().get();
        delete.click();

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();

        _fireConfirm(confirmDialog);

        assertThat(GridKt._size(categoriesGrid)).isEqualTo(12);
    }

    @Test
    void assign_athelete() {
        Tabs tabs = _get(Tabs.class);
        Tab athletes = _get(Tab.class, spec -> spec.withText("Athletes"));
        tabs.setSelectedTab(athletes);

        Grid<AthleteRecord> athletesGrid = _get(Grid.class, spec -> spec.withId("athletes-grid"));
        assertThat(GridKt._size(athletesGrid)).isEqualTo(85);

        AthleteRecord athleteRecord = GridKt._get(athletesGrid, 0);
        assertThat(athleteRecord.getLastName()).isEqualTo("Scholer");

        Button assignAthlete = _get(Button.class, spec -> spec.withId("assign-athlete"));
        assignAthlete.click();

        _assert(Dialog.class, 1);

        _get(TextField.class, spec -> spec.withCaption("Filter").withValue("")).setValue("z");

        Grid<AthleteRecord> searchAthletesGrid = _get(Grid.class, spec -> spec.withId("search-athletes-grid"));
        assertThat(GridKt._size(searchAthletesGrid)).isEqualTo(1);

        athleteRecord = GridKt._get(searchAthletesGrid, 0);
        assertThat(athleteRecord.getLastName()).isEqualTo("Zumstein");

        HorizontalLayout edit = (HorizontalLayout) GridKt._getCellComponent(searchAthletesGrid, 0, "Edit");
        Button assign = (Button) edit.getChildren().filter(component -> component instanceof Button).findFirst().get();
        assign.click();

        assertThat(GridKt._size(athletesGrid)).isEqualTo(86);

        athleteRecord = GridKt._get(athletesGrid, 40);
        assertThat(athleteRecord.getLastName()).isEqualTo("Zumstein");

        HorizontalLayout remove = (HorizontalLayout) GridKt._getCellComponent(athletesGrid, 40, "Remove");
        Button removeButton = (Button) remove.getChildren().filter(component -> component instanceof Button).findFirst().get();
        removeButton.click();

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();

        _fireConfirm(confirmDialog);

        assertThat(GridKt._size(athletesGrid)).isEqualTo(85);
    }

}
