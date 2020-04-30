package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.SeriesRankingAthlete;
import ch.jtaf.reporting.data.SeriesRankingCategory;
import ch.jtaf.reporting.data.SeriesRankingData;
import ch.jtaf.reporting.data.SeriesRankingResult;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.itextpdf.text.PageSize.A4;

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
                        messages.getString("series.ranking"), ranking.getName(), ""));
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
        for (SeriesRankingCategory category : ranking.getCategories()) {
            if (numberOfRows > 20) {
                document.newPage();
            }
            PdfPTable table = createAthletesTable();
            createCategoryTitle(table, category);
            numberOfRows += 2;

            int position = 1;
            for (SeriesRankingAthlete athlete : category.getAthletes()) {
                if (numberOfRows > 23) {
                    document.add(table);
                    document.newPage();
                    table = createAthletesTable();
                }
                createAthleteRow(table, position, athlete);
                position++;
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
        addCategoryTitleCellWithColspan(table, category.getAbbreviation(), 1);
        addCategoryTitleCellWithColspan(table, category.getName() + " "
                + category.getYearFrom() + " - " + category.getYearTo(), 5);

        addCategoryTitleCellWithColspan(table, " ", 6);
    }

    private void createAthleteRow(PdfPTable table, int position, SeriesRankingAthlete athlete) {
        addCell(table, position + ".");
        addCell(table, athlete.getLastName());
        addCell(table, athlete.getFirstName());
        addCell(table, athlete.getYearOfBirth() + "");
        addCell(table, athlete.getClubId() == null ? "" : clubs.get(athlete.getClubId()));
        addCellAlignRight(table, athlete.getTotalPoints() + "");

        StringBuilder sb = new StringBuilder();
        for (SeriesRankingResult result : athlete.getResults()) {
            sb.append(result.getCompetitionName());
            sb.append(": ");
            sb.append(result.getPoints());
            sb.append(" ");
        }
        addCell(table, "");
        addResultsCell(table, sb.toString());
    }
}
