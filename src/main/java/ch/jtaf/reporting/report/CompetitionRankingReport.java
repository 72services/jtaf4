package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.CompetitionRankingData;
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

@SuppressWarnings("DuplicatedCode")
public class CompetitionRankingReport extends RankingReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompetitionRankingReport.class);

    private final CompetitionRankingData ranking;
    private Document document;

    public CompetitionRankingReport(CompetitionRankingData ranking, Locale locale) {
        super(locale);
        this.ranking = ranking;
    }

    public byte[] create() {
        try {
            byte[] ba;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                float border = cmToPixel(1.5f);
                document = new Document(PageSize.A4, border, border, border, border);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
                pdfWriter.setPageEvent(new HeaderFooter(messages.getString("Competition.Ranking"), ranking.name(),
                    DATE_TIME_FORMATTER.format(ranking.competitionDate())));
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
        for (CompetitionRankingData.Category category : ranking.categories()) {
            if (numberOfRows > 20) {
                document.newPage();
            }
            PdfPTable table = createAthletesTable();
            createCategoryTitle(table, category);
            numberOfRows += 2;

            int rank = 1;
            for (CompetitionRankingData.Category.Athlete athlete : category.sortedAthletes()) {
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

    private int calculateNumberOfMedals(CompetitionRankingData.Category category) {
        double numberOfMedals = 0;
        if (ranking.medalPercentage() > 0) {
            double percentage = ranking.medalPercentage();
            numberOfMedals = category.sortedAthletes().size() * (percentage / 100);
            if (numberOfMedals < 3 && ranking.alwaysFirstThreeMedals()) {
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

    private void createCategoryTitle(PdfPTable table, CompetitionRankingData.Category category) {
        addCategoryTitleCellWithColspan(table, category.abbreviation(), 1);
        addCategoryTitleCellWithColspan(table, category.name() + " " + category.yearFrom() + " - " + category.yearTo(), 5);

        addCategoryTitleCellWithColspan(table, " ", 6);
    }

    private void createAthleteRow(PdfPTable table, int rank, CompetitionRankingData.Category.Athlete athlete, int numberOfMedals) {
        if (rank <= numberOfMedals) {
            addCell(table, "* " + rank + ".");
        } else {
            addCell(table, rank + ".");
        }
        addCell(table, athlete.lastName());
        addCell(table, athlete.firstName());
        addCell(table, athlete.yearOfBirth() + "");
        addCell(table, athlete.club());
        addCellAlignRight(table, athlete.totalPoints() + "");

        StringBuilder sb = new StringBuilder();
        for (CompetitionRankingData.Category.Athlete.Result result : athlete.sortedResults()) {
            sb.append(result.eventAbbreviation());
            sb.append(": ");
            sb.append(result.result());
            sb.append(" (");
            sb.append(result.points());
            sb.append(") ");
        }
        addCell(table, "");
        addResultsCell(table, sb.toString());
    }

}
