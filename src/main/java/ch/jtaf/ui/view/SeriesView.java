package ch.jtaf.ui.view;

import ch.jtaf.context.ApplicationContextHolder;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.dialog.CategoryDialog;
import ch.jtaf.ui.dialog.CompetitionDialog;
import ch.jtaf.ui.dialog.SearchAthleteDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.context.ApplicationContextHolder.getBean;
import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@PageTitle("JTAF - Organizations")
@Route(layout = MainLayout.class)
public class SeriesView extends ProtectedView implements HasUrlParameter<String> {

    private final DSLContext dsl;

    private SeriesRecord seriesRecord;

    private Grid<CompetitionRecord> competitionsGrid;
    private Grid<CategoryRecord> categoriesGrid;
    private Grid<AthleteRecord> athletesGrid;

    Tabs sectionTabs = new Tabs();

    private Binder<SeriesRecord> binder = new Binder<>();

    private Map<Long, ClubRecord> clubRecordMap;

    public SeriesView(DSLContext dsl) {
        this.dsl = dsl;

        H3 h3Title = new H3(getTranslation("Series"));
        h3Title.getStyle().set("margin-top", "0px");
        add(h3Title);

        FormLayout formLayout = new FormLayout();
        add(formLayout);

        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name)
                .withValidator(new NotEmptyValidator(this))
                .bind(SeriesRecord::getName, SeriesRecord::setName);

        Checkbox hidden = new Checkbox(getTranslation("Hidden"));
        formLayout.add(hidden);

        binder.forField(hidden)
                .bind(SeriesRecord::getHidden, SeriesRecord::setHidden);

        Checkbox locked = new Checkbox(getTranslation("Locked"));
        formLayout.add(locked);

        binder.forField(locked)
                .bind(SeriesRecord::getLocked, SeriesRecord::setLocked);

        Button save = new Button(getTranslation("Save"));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            TransactionTemplate transactionTemplate = ApplicationContextHolder.getBean(TransactionTemplate.class);
            transactionTemplate.executeWithoutResult((transactionStatus) -> {
                dsl.attach(binder.getBean());
                binder.getBean().store();
            });
        });
        add(save);

        sectionTabs.setWidthFull();
        add(sectionTabs);

        createCompetitionsSection();
        createCategoriesSection();
        createAthletesSection();

        Div grids = new Div(competitionsGrid, categoriesGrid, athletesGrid);
        grids.setWidthFull();
        grids.setHeightFull();
        add(grids);

        Tab tabCompetitions = new Tab(getTranslation("Competitions"));
        sectionTabs.add(tabCompetitions);
        Tab tabCategories = new Tab(getTranslation("Categories"));
        sectionTabs.add(tabCategories);
        Tab tabAthletes = new Tab(getTranslation("Athletes"));
        sectionTabs.add(tabAthletes);

        Map<Tab, Grid<? extends UpdatableRecord<?>>> tabsToGrids = new HashMap<>();
        tabsToGrids.put(tabCompetitions, competitionsGrid);
        tabsToGrids.put(tabCategories, categoriesGrid);
        tabsToGrids.put(tabAthletes, athletesGrid);

        categoriesGrid.setVisible(false);
        athletesGrid.setVisible(false);

        sectionTabs.addSelectedChangeListener(event -> {
            tabsToGrids.values().forEach(grid -> grid.setVisible(false));

            var selectedGrid = tabsToGrids.get(sectionTabs.getSelectedTab());
            selectedGrid.setVisible(true);
        });
    }

    private void createCompetitionsSection() {
        CompetitionDialog dialog = new CompetitionDialog(getTranslation("Category"));

        competitionsGrid = new Grid<>();
        competitionsGrid.setHeightFull();
        competitionsGrid.addColumn(CompetitionRecord::getName).setHeader(getTranslation("Name")).setSortable(true);
        competitionsGrid.addColumn(CompetitionRecord::getCompetitionDate).setHeader(getTranslation("Date")).setSortable(true);

        addActionColumnAndSetSelectionListener(competitionsGrid, dialog, this::loadData, () -> {
            CompetitionRecord newRecord = COMPETITION.newRecord();
            newRecord.setSeriesId(seriesRecord.getId());
            return newRecord;
        });
    }

    private void createCategoriesSection() {
        CategoryDialog dialog = new CategoryDialog(getTranslation("Category"));

        categoriesGrid = new Grid<>();
        categoriesGrid.setHeightFull();
        categoriesGrid.addColumn(CategoryRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true);
        categoriesGrid.addColumn(CategoryRecord::getName).setHeader(getTranslation("Name")).setSortable(true);
        categoriesGrid.addColumn(CategoryRecord::getYearFrom).setHeader(getTranslation("Year.From")).setSortable(true);
        categoriesGrid.addColumn(CategoryRecord::getYearTo).setHeader(getTranslation("Year.To")).setSortable(true);

        addActionColumnAndSetSelectionListener(categoriesGrid, dialog, this::loadData, () -> {
            CategoryRecord newRecord = CATEGORY.newRecord();
            newRecord.setSeriesId(seriesRecord.getId());
            return newRecord;
        });
    }

    private void createAthletesSection() {
        SearchAthleteDialog dialog = new SearchAthleteDialog();

        athletesGrid = new Grid<>();
        athletesGrid.setHeightFull();
        athletesGrid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true);
        athletesGrid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true);
        athletesGrid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true);
        athletesGrid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true);
        athletesGrid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
                : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club"));

        Button assign = new Button(athletesGrid.getTranslation("Assign.Athelete"));
        assign.addClickListener(event -> dialog.open());

        athletesGrid.addComponentColumn(record -> {
            Button remove = new Button(athletesGrid.getTranslation("Remove"));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            remove.addClickListener(event -> {
                getBean(TransactionTemplate.class).executeWithoutResult(transactionStatus -> {
                    removeAtheleteFromSeries(record);
                });
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(remove);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(assign);
    }

    private void removeAtheleteFromSeries(UpdatableRecord<?> record) {
        AthleteRecord athleteRecord = (AthleteRecord) record;
        dsl
                .deleteFrom(CATEGORY_ATHLETE)
                .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(athleteRecord.getId()))
                .and(CATEGORY_ATHLETE.CATEGORY_ID.in(dsl.select(CATEGORY.ID).from(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesRecord.getId()))));
    }

    @Override
    void loadData() {
        var competitionRecords = dsl.selectFrom(COMPETITION).where(COMPETITION.SERIES_ID.eq(seriesRecord.getId())).orderBy(COMPETITION.COMPETITION_DATE).fetch();
        competitionsGrid.setItems(competitionRecords);

        var categoryRecords = dsl.selectFrom(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesRecord.getId())).orderBy(CATEGORY.ABBREVIATION).fetch();
        categoriesGrid.setItems(categoryRecords);

        var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).fetch();
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

        var athleteRecords = dsl
                .select()
                .from(ATHLETE)
                .join(CATEGORY_ATHLETE).on(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
                .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                .where(CATEGORY.SERIES_ID.eq(seriesRecord.getId()))
                .orderBy(CATEGORY.ABBREVIATION, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME)
                .fetchInto(ATHLETE);
        athletesGrid.setItems(athleteRecords);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter == null) {
            seriesRecord = SERIES.newRecord();
            seriesRecord.setOrganizationId(organizationRecord.getId());
        } else {
            long seriesId = Long.parseLong(parameter);
            seriesRecord = dsl.selectFrom(SERIES).where(SERIES.ID.eq(seriesId)).fetchOne();
        }
        binder.setBean(seriesRecord);
    }

}
