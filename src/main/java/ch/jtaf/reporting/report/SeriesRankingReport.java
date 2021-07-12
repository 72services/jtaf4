package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.SeriesRankingData;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import static com.lowagie.text.PageSize.A4;

public class SeriesRankingReport extends RankingReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesRankingReport.class);

    private final SeriesRankingData ranking;
    private Document document;

    public SeriesRankingReport(SeriesRankingData ranking, Locale locale) {
        super(locale);
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
        for (SeriesRankingData.Category category : ranking.categories()) {
            if (numberOfRows > 20) {
                document.newPage();
            }
            PdfPTable table = createAthletesTable();
            createCategoryTitle(table, category);
            numberOfRows += 2;

            int rank = 1;
            for (SeriesRankingData.Category.Athlete athlete : category.getFilteredAndSortedAthletes(ranking.numberOfCompetitions())) {
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

    private void createCategoryTitle(PdfPTable table, SeriesRankingData.Category category) {
        addCategoryTitleCellWithColspan(table, category.abbreviation(), 1);
        addCategoryTitleCellWithColspan(table, category.name() + " "
            + category.yearFrom() + " - " + category.yearTo(), 5);

        addCategoryTitleCellWithColspan(table, " ", 6);
    }

    private void createAthleteRow(PdfPTable table, int rank, SeriesRankingData.Category.Athlete athlete) {
        addCell(table, rank + ".");
        addCell(table, athlete.lastName());
        addCell(table, athlete.firstName());
        addCell(table, athlete.yearOfBirth() + "");
        addCell(table, athlete.club() != null ? athlete.club() : "");
        addCellAlignRight(table, athlete.totalPoints() + "");

        StringBuilder sb = new StringBuilder();
        for (SeriesRankingData.Category.Athlete.Result result : athlete.results()) {
            sb.append(result.competitionName());
            sb.append(": ");
            sb.append(result.points());
            sb.append(" ");
        }
        addCell(table, "");
        addResultsCell(table, sb.toString());
    }
}
