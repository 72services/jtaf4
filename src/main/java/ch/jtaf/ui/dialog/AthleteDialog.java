package ch.jtaf.ui.dialog;

import ch.jtaf.context.ApplicationContextHolder;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.model.Gender;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Club.CLUB;

public class AthleteDialog extends EditDialog<AthleteRecord> {

    private Map<Long, ClubRecord> clubRecordMap = new HashMap<>();

    private Select<ClubRecord> club;

    public AthleteDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField firstName = new TextField(getTranslation("First.Name"));
        firstName.setRequiredIndicatorVisible(true);
        formLayout.add(firstName);

        binder.forField(firstName)
                .withValidator(notEmptyValidator())
                .bind(AthleteRecord::getFirstName, AthleteRecord::setFirstName);

        TextField lastName = new TextField(getTranslation("Last.Name"));
        lastName.setRequiredIndicatorVisible(true);
        formLayout.add(lastName);

        binder.forField(lastName)
                .withValidator(notEmptyValidator())
                .bind(AthleteRecord::getLastName, AthleteRecord::setLastName);

        Select<String> gender = new Select<>(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());
        formLayout.add(gender);

        binder.forField(gender)
                .bind(AthleteRecord::getGender, AthleteRecord::setGender);

        TextField yearOfBirth = new TextField(getTranslation("Year"));
        lastName.setRequiredIndicatorVisible(true);
        formLayout.add(lastName);

        binder.forField(yearOfBirth)
                .withConverter(new StringToIntegerConverter(getTranslation("Must.be.a.number")))
                .bind(AthleteRecord::getYearOfBirth, AthleteRecord::setYearOfBirth);

        club = new Select<>();
        club.setLabel(getTranslation("Club"));
        club.setRequiredIndicatorVisible(true);
        club.setItemLabelGenerator(item -> item.getAbbreviation() + " " + item.getName());
        club.setItems(getClubs());
        formLayout.add(club);

        binder.forField(club)
                .withConverter(new Converter<ClubRecord, Long>() {
                    @Override
                    public Result<Long> convertToModel(ClubRecord value, ValueContext context) {
                        return Result.ok(value == null ? null : value.getId());
                    }

                    @Override
                    public ClubRecord convertToPresentation(Long value, ValueContext context) {
                        return clubRecordMap.get(value);
                    }
                })
                .bind(AthleteRecord::getClubId, AthleteRecord::setClubId);
    }

    private List<ClubRecord> getClubs() {
        OrganizationRecord organizationRecord = UI.getCurrent().getSession().getAttribute(OrganizationRecord.class);

        if (organizationRecord == null) {
            return Collections.emptyList();
        } else {
            DSLContext dsl = ApplicationContextHolder.getBean(DSLContext.class);
            var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).fetch();
            clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));
            return clubs;
        }
    }

}
