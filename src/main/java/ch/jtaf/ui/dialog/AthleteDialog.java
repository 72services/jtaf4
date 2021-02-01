package ch.jtaf.ui.dialog;

import ch.jtaf.context.ApplicationContextHolder;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.model.Gender;
import ch.jtaf.ui.security.OrganizationHolder;
import ch.jtaf.ui.validator.NotEmptyValidator;
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

    private static final long serialVersionUID = 1L;

    private Map<Long, ClubRecord> clubRecordMap = new HashMap<>();

    public AthleteDialog(String title) {
        super(title);
    }

    @SuppressWarnings({"DuplicatedCode"})
    @Override
    public void createForm() {
        TextField lastName = new TextField(getTranslation("Last.Name"));
        lastName.setRequiredIndicatorVisible(true);

        binder.forField(lastName)
            .withValidator(new NotEmptyValidator(this))
            .bind(AthleteRecord::getLastName, AthleteRecord::setLastName);

        TextField firstName = new TextField(getTranslation("First.Name"));
        firstName.setRequiredIndicatorVisible(true);

        binder.forField(firstName)
            .withValidator(new NotEmptyValidator(this))
            .bind(AthleteRecord::getFirstName, AthleteRecord::setFirstName);

        Select<String> gender = new Select<>();
        gender.setLabel(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());

        binder.forField(gender)
            .bind(AthleteRecord::getGender, AthleteRecord::setGender);

        TextField yearOfBirth = new TextField(getTranslation("Year"));
        lastName.setRequiredIndicatorVisible(true);

        binder.forField(yearOfBirth)
            .withConverter(new StringToIntegerConverter(getTranslation("Must.be.a.number")))
            .bind(AthleteRecord::getYearOfBirth, AthleteRecord::setYearOfBirth);

        Select<ClubRecord> club = new Select<>();
        club.setLabel(getTranslation("Club"));
        club.setRequiredIndicatorVisible(true);
        club.setItemLabelGenerator(item -> item.getAbbreviation() + " " + item.getName());
        club.setItems(getClubs());

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

        TextField year = new TextField("Year");
        year.setRequiredIndicatorVisible(true);

        binder.forField(year)
            .withConverter(new StringToIntegerConverter(getTranslation("Must.be.a.number")))
            .bind(AthleteRecord::getYearOfBirth, AthleteRecord::setYearOfBirth);

        formLayout.add(lastName, firstName, gender, year, club);
    }

    private List<ClubRecord> getClubs() {
        OrganizationRecord organizationRecord = OrganizationHolder.getOrganization();

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
