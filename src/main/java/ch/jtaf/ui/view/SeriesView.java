package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.CategoryAthleteRecord;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.service.NumberAndSheetsService;
import ch.jtaf.ui.dialog.CategoryDialog;
import ch.jtaf.ui.dialog.CompetitionDialog;
import ch.jtaf.ui.dialog.SearchAthleteDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.ui.security.OrganizationProvider;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@Route(layout = MainLayout.class)
public class SeriesView extends ProtectedView implements HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String BLANK = "_blank";

    private final transient NumberAndSheetsService numberAndSheetsService;
    private final TransactionTemplate transactionTemplate;

    private SeriesRecord seriesRecord;

    private Grid<CompetitionRecord> competitionsGrid;
    private Grid<CategoryRecord> categoriesGrid;
    private Grid<AthleteRecord> athletesGrid;

    final Tabs sectionTabs = new Tabs();

    private final transient Binder<SeriesRecord> binder = new Binder<>();

    private Map<Long, ClubRecord> clubRecordMap;

    public SeriesView(DSLContext dsl, TransactionTemplate transactionTemplate, NumberAndSheetsService numberAndSheetsService, OrganizationProvider organizationProvider) {
        super(dsl, organizationProvider);
        this.transactionTemplate = transactionTemplate;
        this.numberAndSheetsService = numberAndSheetsService;

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
        save.addClickListener(event ->
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                dsl.attach(binder.getBean());
                binder.getBean().store();
            })
        );
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

    @Override
    protected void refreshAll() {
        var competitionRecords = dsl.selectFrom(COMPETITION).where(COMPETITION.SERIES_ID.eq(seriesRecord.getId()))
            .orderBy(COMPETITION.COMPETITION_DATE).fetch();
        competitionsGrid.setItems(competitionRecords);

        var categoryRecords = dsl.selectFrom(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesRecord.getId()))
            .orderBy(CATEGORY.ABBREVIATION).fetch();
        categoriesGrid.setItems(categoryRecords);

        var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).fetch();
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

        var athleteRecords = dsl
            .select(CATEGORY_ATHLETE.athlete().fields())
            .from(CATEGORY_ATHLETE)
            .where(CATEGORY_ATHLETE.category().SERIES_ID.eq(seriesRecord.getId()))
            .orderBy(CATEGORY_ATHLETE.category().ABBREVIATION, CATEGORY_ATHLETE.athlete().LAST_NAME, CATEGORY_ATHLETE.athlete().FIRST_NAME)
            .fetchInto(ATHLETE);
        athletesGrid.setItems(athleteRecords);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter == null) {
            organizationRecord = organizationProvider.getOrganization();

            seriesRecord = SERIES.newRecord();
            seriesRecord.setOrganizationId(organizationRecord.getId());
        } else {
            long seriesId = Long.parseLong(parameter);
            seriesRecord = dsl.selectFrom(SERIES).where(SERIES.ID.eq(seriesId)).fetchOne();
        }
        binder.setBean(seriesRecord);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Series");
    }

    private void createCompetitionsSection() {
        CompetitionDialog dialog = new CompetitionDialog(getTranslation("Category"));

        competitionsGrid = new Grid<>();
        competitionsGrid.setId("competitions-grid");
        competitionsGrid.setHeightFull();
        competitionsGrid.addColumn(CompetitionRecord::getName).setHeader(getTranslation("Name")).setSortable(true);
        competitionsGrid.addColumn(CompetitionRecord::getCompetitionDate).setHeader(getTranslation("Date")).setSortable(true);
        competitionsGrid.addColumn(new ComponentRenderer<>(competition -> {
            Anchor sheetsOrderedByAthlete = new Anchor(new StreamResource("sheets_orderby_athlete" + competition.getId() + ".pdf",
                () -> {
                    byte[] pdf = numberAndSheetsService.createSheets(competition.getSeriesId(), competition.getId(),
                        CATEGORY.ABBREVIATION, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME);
                    return new ByteArrayInputStream(pdf);
                }), getTranslation("Sheets"));
            sheetsOrderedByAthlete.setTarget(BLANK);

            Anchor sheetsOrderedByClub = new Anchor(new StreamResource("sheets_orderby_club" + competition.getId() + ".pdf",
                () -> {
                    byte[] pdf = numberAndSheetsService.createSheets(competition.getSeriesId(), competition.getId(),
                        CLUB.ABBREVIATION, CATEGORY.ABBREVIATION, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME);
                    return new ByteArrayInputStream(pdf);
                }), getTranslation("Ordered.by.club"));
            sheetsOrderedByClub.setTarget(BLANK);

            Anchor numbersOrderedByAthlete = new Anchor(new StreamResource("numbers_orderby_athlete" + competition.getId() + ".pdf",
                () -> {
                    byte[] pdf = numberAndSheetsService.createNumbers(competition.getSeriesId(),
                        CATEGORY.ABBREVIATION, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME);
                    return new ByteArrayInputStream(pdf);
                }), getTranslation("Numbers"));
            numbersOrderedByAthlete.setTarget(BLANK);

            Anchor numbersOrderedByClub = new Anchor(new StreamResource("numbers_orderby_club" + competition.getId() + ".pdf",
                () -> {
                    byte[] pdf = numberAndSheetsService.createNumbers(competition.getSeriesId(),
                        CLUB.ABBREVIATION, CATEGORY.ABBREVIATION, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME);
                    return new ByteArrayInputStream(pdf);
                }), getTranslation("Ordered.by.club"));
            numbersOrderedByClub.setTarget(BLANK);

            return new HorizontalLayout(sheetsOrderedByAthlete, sheetsOrderedByClub, numbersOrderedByAthlete, numbersOrderedByClub);
        }));

        addActionColumnAndSetSelectionListener(competitionsGrid, dialog, competitionRecord -> refreshAll(), () -> {
            CompetitionRecord newRecord = COMPETITION.newRecord();
            newRecord.setMedalPercentage(0);
            newRecord.setSeriesId(seriesRecord.getId());
            return newRecord;
        });
    }

    private void createCategoriesSection() {
        CategoryDialog dialog = new CategoryDialog(getTranslation("Category"));

        categoriesGrid = new Grid<>();
        categoriesGrid.setId("categories-grid");
        categoriesGrid.setHeightFull();
        categoriesGrid.addColumn(CategoryRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true);
        categoriesGrid.addColumn(CategoryRecord::getName).setHeader(getTranslation("Name")).setSortable(true);
        categoriesGrid.addColumn(CategoryRecord::getYearFrom).setHeader(getTranslation("Year.From")).setSortable(true);
        categoriesGrid.addColumn(CategoryRecord::getYearTo).setHeader(getTranslation("Year.To")).setSortable(true);
        categoriesGrid.addColumn(new ComponentRenderer<>(category -> {
            Anchor sheet = new Anchor(new StreamResource("sheet" + category.getId() + ".pdf",
                () -> {
                    byte[] pdf = numberAndSheetsService.createEmptySheets(seriesRecord.getId(), category.getId());
                    return new ByteArrayInputStream(pdf);
                }), getTranslation("Sheets"));
            sheet.setTarget(BLANK);

            return new HorizontalLayout(sheet);
        }));

        addActionColumnAndSetSelectionListener(categoriesGrid, dialog, categoryRecord -> refreshAll(), () -> {
            CategoryRecord newRecord = CATEGORY.newRecord();
            newRecord.setSeriesId(seriesRecord.getId());
            return newRecord;
        });
    }

    private void createAthletesSection() {
        athletesGrid = new Grid<>();
        athletesGrid.setId("athletes-grid");
        athletesGrid.setHeightFull();
        athletesGrid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true);
        athletesGrid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true);
        athletesGrid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true);
        athletesGrid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true);
        athletesGrid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
            : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club"));

        Button assign = new Button(getTranslation("Assign.Athlete"));
        assign.addClickListener(event -> {
            SearchAthleteDialog dialog = new SearchAthleteDialog(dsl, organizationRecord.getId(), seriesRecord.getId(), this::onAthleteSelect);
            dialog.open();
        });

        athletesGrid.addComponentColumn(athleteRecord -> {
            Button remove = new Button(getTranslation("Remove"));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            remove.addClickListener(event -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(getTranslation("Confirm"),
                    getTranslation("Are.you.sure"),
                    getTranslation("Remove"), e -> removeAthleteFromSeries(athleteRecord),
                    getTranslation("Cancel"), e -> {
                });
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.open();
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(remove);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(assign);
    }

    private void onAthleteSelect(AthleteRecord athleteRecord) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Long categoryId = dsl
                .select(CATEGORY.ID)
                .from(CATEGORY)
                .where(CATEGORY.SERIES_ID.eq(seriesRecord.getId()))
                .and(CATEGORY.GENDER.eq(athleteRecord.getGender()))
                .and(CATEGORY.YEAR_FROM.le(athleteRecord.getYearOfBirth()))
                .and(CATEGORY.YEAR_TO.ge(athleteRecord.getYearOfBirth()))
                .fetchOneInto(Long.class);

            CategoryAthleteRecord categoryAthleteRecord = CATEGORY_ATHLETE.newRecord();
            categoryAthleteRecord.setAthleteId(athleteRecord.getId());
            categoryAthleteRecord.setCategoryId(categoryId);
            categoryAthleteRecord.attach(dsl.configuration());
            categoryAthleteRecord.store();
        });

        refreshAll();
    }

    private void removeAthleteFromSeries(UpdatableRecord<?> updatableRecord) {
        AthleteRecord athleteRecord = (AthleteRecord) updatableRecord;
        dsl
            .deleteFrom(CATEGORY_ATHLETE)
            .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(athleteRecord.getId()))
            .and(CATEGORY_ATHLETE.CATEGORY_ID.in(dsl.select(CATEGORY.ID).from(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesRecord.getId()))))
            .execute();
    }

}
