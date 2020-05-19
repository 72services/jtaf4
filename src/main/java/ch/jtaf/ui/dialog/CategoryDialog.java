package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.textfield.TextField;

public class CategoryDialog extends EditDialog<CategoryRecord> {

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

        formLayout.add(abbreviation, name);
    }
}
