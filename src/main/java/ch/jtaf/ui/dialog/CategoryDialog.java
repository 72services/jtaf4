package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.model.CategoryEventVO;
import ch.jtaf.model.Gender;
import ch.jtaf.ui.converter.JtafStringToIntegerConverter;
import ch.jtaf.ui.function.Callback;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serial;
import java.util.Collections;
import java.util.List;

import static ch.jtaf.context.ApplicationContextHolder.getBean;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Event.EVENT;

public class CategoryDialog extends EditDialog<CategoryRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private Grid<CategoryEventVO> categoryEventsGrid;

    public CategoryDialog(String title) {
        super(title, "1600px");
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void createForm() {
        TextField abbreviation = new TextField(getTranslation("Abbreviation"));
        abbreviation.setRequiredIndicatorVisible(true);

        binder.forField(abbreviation)
            .withValidator(new NotEmptyValidator(this))
            .bind(CategoryRecord::getAbbreviation, CategoryRecord::setAbbreviation);

        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(CategoryRecord::getName, CategoryRecord::setName);

        Select<String> gender = new Select<>();
        gender.setLabel(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());

        binder.forField(gender)
            .bind(CategoryRecord::getGender, CategoryRecord::setGender);

        TextField yearFrom = new TextField(getTranslation("Year.From"));
        yearFrom.setRequiredIndicatorVisible(true);

        binder.forField(yearFrom)
            .withConverter(new JtafStringToIntegerConverter("May.not.be.empty"))
            .withNullRepresentation(-1)
            .bind(CategoryRecord::getYearFrom, CategoryRecord::setYearFrom);

        TextField yearTo = new TextField(getTranslation("Year.To"));
        yearTo.setRequiredIndicatorVisible(true);

        binder.forField(yearTo)
            .withConverter(new JtafStringToIntegerConverter("May.not.be.empty"))
            .withNullRepresentation(-1)
            .bind(CategoryRecord::getYearTo, CategoryRecord::setYearTo);

        formLayout.add(abbreviation, name, gender, yearFrom, yearTo);

        categoryEventsGrid = new Grid<>();
        categoryEventsGrid.addColumn(CategoryEventVO::abbreviation).setHeader(getTranslation("Abbreviation"));
        categoryEventsGrid.addColumn(CategoryEventVO::name).setHeader(getTranslation("Name"));
        categoryEventsGrid.addColumn(CategoryEventVO::gender).setHeader(getTranslation("Gender"));
        categoryEventsGrid.addColumn(CategoryEventVO::eventType).setHeader(getTranslation("Event.Type"));
        categoryEventsGrid.addColumn(CategoryEventVO::A).setHeader("A");
        categoryEventsGrid.addColumn(CategoryEventVO::B).setHeader("B");
        categoryEventsGrid.addColumn(CategoryEventVO::C).setHeader("C");
        categoryEventsGrid.addColumn(CategoryEventVO::position).setHeader(getTranslation("Position"));

        Button addEvent = new Button(getTranslation("Add.Event"));
        addEvent.addClickListener(event -> {
            // TODO EventSearchDialog
        });

        categoryEventsGrid.addComponentColumn(categoryRecord -> {
            Button remove = new Button(getTranslation("Remove"));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            remove.addClickListener(event ->
                getBean(TransactionTemplate.class).executeWithoutResult(transactionStatus -> removeEventFromCategory(categoryRecord)));

            HorizontalLayout horizontalLayout = new HorizontalLayout(remove);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(addEvent);

        add(categoryEventsGrid);
    }

    @Override
    public void open(UpdatableRecord<?> updatableRecord, Callback afterSave) {
        super.open(updatableRecord, afterSave);

        categoryEventsGrid.setItems(getCategoryEvents());
    }

    private List<CategoryEventVO> getCategoryEvents() {
        if (binder.getBean() != null) {
            return getBean(DSLContext.class)
                .select(EVENT.ABBREVIATION, EVENT.NAME, EVENT.GENDER, EVENT.EVENT_TYPE, EVENT.A, EVENT.B, EVENT.C, CATEGORY_EVENT.POSITION,
                    CATEGORY_EVENT.CATEGORY_ID, CATEGORY_EVENT.EVENT_ID)
                .from(CATEGORY_EVENT)
                .join(EVENT).on(EVENT.ID.eq(CATEGORY_EVENT.EVENT_ID))
                .where(CATEGORY_EVENT.CATEGORY_ID.eq(binder.getBean().getId()))
                .orderBy(CATEGORY_EVENT.POSITION)
                .fetchInto(CategoryEventVO.class);
        } else {
            return Collections.emptyList();
        }
    }

    private void removeEventFromCategory(CategoryEventVO categoryEventVO) {
        ConfirmDialog confirmDialog = new ConfirmDialog(getTranslation("Confirm"),
            getTranslation("Are.you.sure"),
            getTranslation("Remove"), event ->
            getBean(TransactionTemplate.class).executeWithoutResult(transactionStatus ->
                getBean(DSLContext.class)
                    .deleteFrom(CATEGORY_EVENT)
                    .where(CATEGORY_EVENT.CATEGORY_ID.eq(categoryEventVO.categoryId()))
                    .and(CATEGORY_EVENT.EVENT_ID.eq(categoryEventVO.eventId()))
                    .execute()),
            getTranslation("Cancel"), event -> {
        });
        confirmDialog.setConfirmButtonTheme("error primary");
        confirmDialog.open();
    }
}
