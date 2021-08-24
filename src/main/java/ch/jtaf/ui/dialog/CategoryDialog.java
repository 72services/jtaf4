package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.io.Serial;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class CategoryDialog extends EditDialog<CategoryRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

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
        gender.setItems(List.of("0", "1"));
        gender.setValue("0");
        gender.setRequiredIndicatorVisible(true);

        binder.forField(gender)
            .bind(CategoryRecord::getGender, CategoryRecord::setGender);

        TextField yearFrom = new TextField(getTranslation("Year.From"));
        yearFrom.setRequiredIndicatorVisible(true);

        binder.forField(yearFrom)
            .withConverter(new StringToIntegerConverter("May.not.be.empty"))
            .withNullRepresentation(-1)
            .bind(CategoryRecord::getYearFrom, CategoryRecord::setYearFrom);

        TextField yearTo = new TextField(getTranslation("Year.To"));
        yearTo.setRequiredIndicatorVisible(true);

        binder.forField(yearTo)
            .withConverter(new StringToIntegerConverter("May.not.be.empty"))
            .withNullRepresentation(-1)
            .bind(CategoryRecord::getYearTo, CategoryRecord::setYearTo);

        formLayout.add(abbreviation, name, gender, yearFrom, yearTo);
    }
}
