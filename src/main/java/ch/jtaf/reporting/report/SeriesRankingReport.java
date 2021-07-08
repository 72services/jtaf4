package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.SeriesRankingAthlete;
import ch.jtaf.reporting.data.SeriesRankingCategory;
import ch.jtaf.reporting.data.SeriesRankingData;
import ch.jtaf.reporting.data.SeriesRankingResult;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.lowagie.text.PageSize.A4;

public class SeriesRankingReport extends RankingReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesRankingReport.class);

    private final SeriesRankingData ranking;
    private Document document;

    public SeriesRankingReport(SeriesRankingData ranking, Locale locale, Map<Long, String> clubs) {
        super(locale, clubs);
        this.ranking = ranking;
    }

    public byte[] create() {
        try {
            byte[] ba;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                document = new Document(A4);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
                pdfWriter.setPageEvent(new HeaderFooter(
                    messages.getString("Series.Ranking"), ranking.name(), ""));
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

    private void createRanking() throws DocumentException {
        for (SeriesRankingCategory category : ranking.categories()) {
            if (numberOfRows > 20) {
                document.newPage();
            }
            PdfPTable table = createAthletesTable();
            createCategoryTitle(table, category);
            numberOfRows += 2;

            int rank = 1;
            for (SeriesRankingAthlete athlete : category.getFilteredAthletes(ranking.numberOfCompetitions())) {
                if (numberOfRows > 23) {
                    document.add(table);
                    document.newPage();
                    table = createAthletesTable();
                }
                createAthleteRow(table, rank, athlete);
                rank++;
                numberOfRows += 1;
            }
            document.add(table);
        }
    }

    private PdfPTable createAthletesTable() {
        PdfPTable table = new PdfPTable(new float[]{2f, 10f, 10f, 2f, 5f, 5f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(cmToPixel(1f));
        return table;
    }

    private void createCategoryTitle(PdfPTable table, SeriesRankingCategory category) {
        addCategoryTitleCellWithColspan(table, category.abbreviation(), 1);
        addCategoryTitleCellWithColspan(table, category.name() + " "
            + category.yearFrom() + " - " + category.yearTo(), 5);

        addCategoryTitleCellWithColspan(table, " ", 6);
    }

    private void createAthleteRow(PdfPTable table, int rank, SeriesRankingAthlete athlete) {
        addCell(table, rank + ".");
        addCell(table, athlete.lastName());
        addCell(table, athlete.firstName());
        addCell(table, athlete.yearOfBirth() + "");
        addCell(table, athlete.clubId() == null ? "" : clubs.get(athlete.clubId()));
        addCellAlignRight(table, athlete.totalPoints() + "");

        StringBuilder sb = new StringBuilder();
        for (SeriesRankingResult result : athlete.sortedResults()) {
            sb.append(result.competitionId());
            sb.append(": ");
            sb.append(result.points());
            sb.append(" ");
        }
        addCell(table, "");
        addResultsCell(table, sb.toString());
    }
}
