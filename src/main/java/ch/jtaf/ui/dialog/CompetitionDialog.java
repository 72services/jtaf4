package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;

public class CompetitionDialog extends EditDialog<CompetitionRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompetitionDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(CompetitionRecord::getName, CompetitionRecord::setName);

        DatePicker date = new DatePicker(getTranslation("Date"));
        name.setRequiredIndicatorVisible(true);

        binder.forField(date)
            .bind(CompetitionRecord::getCompetitionDate, CompetitionRecord::setCompetitionDate);

        formLayout.add(name, date);
    }
}
