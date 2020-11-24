package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.CompetitionRankingAthlete;
import ch.jtaf.reporting.data.CompetitionRankingCategory;
import ch.jtaf.reporting.data.CompetitionRankingData;
import ch.jtaf.reporting.data.CompetitionRankingResult;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("DuplicatedCode")
public class CompetitionRankingReport extends RankingReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompetitionRankingReport.class);

    private final CompetitionRankingData ranking;
    private Document document;

    public CompetitionRankingReport(CompetitionRankingData ranking, Locale locale, Map<Long, String> clubs) {
        super(locale, clubs);
        this.ranking = ranking;
    }

    public byte[] create() {
        try {
            byte[] ba;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                float border = cmToPixel(1.5f);
                document = new Document(PageSize.A4, border, border, border, border);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
                pdfWriter.setPageEvent(new HeaderFooter(messages.getString("Competition.Ranking"), ranking.getName(),
                    DATE_TIME_FORMATTER.format(ranking.getCompetitionDate())));
                document.open();

                createRanking();

                document.close();
                pdfWriter.flush();
                ba = baos.toByteArray();
            }

            return ba;
        } catch (DocumentException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    private void createRanking() {
        for (CompetitionRankingCategory category : ranking.getCategories()) {
            if (numberOfRows > 20) {
                document.newPage();
            }
            PdfPTable table = createAthletesTable();
            createCategoryTitle(table, category);
            numberOfRows += 2;

            int rank = 1;
            for (CompetitionRankingAthlete athlete : category.getAthletes()) {
                if (numberOfRows > 23) {
                    document.add(table);
                    table = createAthletesTable();
                    document.newPage();
                }
                createAthleteRow(table, rank, athlete, calculateNumberOfMedals(category));
                rank++;
                numberOfRows += 1;
            }
            document.add(table);
        }
    }

    private int calculateNumberOfMedals(CompetitionRankingCategory category) {
        double numberOfMedals = 0;
        if (ranking.getMedalPercentage() > 0) {
            double percentage = ranking.getMedalPercentage();
            numberOfMedals = category.getAthletes().size() * (percentage / 100);
            if (numberOfMedals < 3 && ranking.isAlwaysFirstThreeMedals()) {
                numberOfMedals = 3;
            }
        }
        return (int) numberOfMedals;
    }

    private PdfPTable createAthletesTable() {
        PdfPTable table = new PdfPTable(new float[]{2f, 10f, 10f, 2f, 5f, 5f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(cmToPixel(0.6f));
        return table;
    }

    private void createCategoryTitle(PdfPTable table, CompetitionRankingCategory category) {
        addCategoryTitleCellWithColspan(table, category.getAbbreviation(), 1);
        addCategoryTitleCellWithColspan(table,
            category.getName() + " " + category.getYearFrom() + " - " + category.getYearTo(), 5);

        addCategoryTitleCellWithColspan(table, " ", 6);
    }

    private void createAthleteRow(PdfPTable table, int rank, CompetitionRankingAthlete athlete, int numberOfMedals) {
        if (rank <= numberOfMedals) {
            addCell(table, "* " + rank + ".");
        } else {
            addCell(table, rank + ".");
        }
        addCell(table, athlete.getLastName());
        addCell(table, athlete.getFirstName());
        addCell(table, athlete.getYearOfBirth() + "");
        addCell(table, athlete.getClubId() == null ? "" : clubs.get(athlete.getClubId()));
        addCellAlignRight(table, athlete.getTotalPoints() + "");

        StringBuilder sb = new StringBuilder();
        for (CompetitionRankingResult result : athlete.getResults()) {
            sb.append(result.getEventAbbreviation());
            sb.append(": ");
            sb.append(result.getResult());
            sb.append(" (");
            sb.append(result.getPoints());
            sb.append(") ");
        }
        addCell(table, "");
        addResultsCell(table, sb.toString());
    }

}
