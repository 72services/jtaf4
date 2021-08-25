package ch.jtaf.ui.dialog;

import ch.jtaf.context.ApplicationContextHolder;
import ch.jtaf.db.tables.CategoryEvent;
import ch.jtaf.db.tables.Event;
import ch.jtaf.db.tables.records.CategoryEventRecord;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.model.CategoryEventVO;
import ch.jtaf.model.Gender;
import ch.jtaf.ui.converter.JtafStringToIntegerConverter;
import ch.jtaf.ui.function.Callback;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

import java.io.ByteArrayInputStream;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@SuppressWarnings("DuplicatedCode")
public class CategoryDialog extends EditDialog<CategoryRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private Grid<CategoryEventVO> categoryEventsGrid;

    public CategoryDialog(String title) {
        super(title);
    }

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

        setWidth("1000px");

        add(categoryEventsGrid);
    }

    @Override
    public void open(UpdatableRecord<?> record, Callback afterSave) {
        super.open(record, afterSave);

        categoryEventsGrid.setItems(getCategoryEvents());
    }

    private List<CategoryEventVO> getCategoryEvents() {
        if (binder.getBean() != null) {
            DSLContext dsl = ApplicationContextHolder.getBean(DSLContext.class);

            return dsl.select(EVENT.ABBREVIATION, EVENT.NAME, EVENT.GENDER, EVENT.EVENT_TYPE, EVENT.A, EVENT.B, EVENT.C, CATEGORY_EVENT.POSITION)
                .from(CATEGORY_EVENT)
                .join(EVENT).on(EVENT.ID.eq(CATEGORY_EVENT.EVENT_ID))
                .where(CATEGORY_EVENT.CATEGORY_ID.eq(binder.getBean().getId()))
                .orderBy(CATEGORY_EVENT.POSITION)
                .fetchInto(CategoryEventVO.class);
        } else {
            return Collections.emptyList();
        }
    }
}
