package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.EventsRankingData;
import ch.jtaf.reporting.data.EventsRankingEvent;
import ch.jtaf.reporting.data.EventsRankingResult;
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

public class EventsRankingReport extends RankingReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsRankingReport.class);

    private final EventsRankingData ranking;

    private Document document;

    public EventsRankingReport(EventsRankingData ranking, Locale locale, Map<Long, String> clubs) {
        super(locale, clubs);
        this.ranking = ranking;
    }

    public byte[] create() {
        try {
            byte[] ba;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                float border = cmToPixel(1.5f);
                document = new Document(A4, border, border, border, border);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
                pdfWriter.setPageEvent(new HeaderFooter(messages.getString("event.ranking"), ranking.getName(),
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

    private void createRanking() throws DocumentException {
        for (EventsRankingEvent event : ranking.getEvents()) {
            PdfPTable table = new PdfPTable(new float[]{2f, 10f, 10f, 2f, 2f, 5f, 5f});
            table.setWidthPercentage(100);
            table.setSpacingBefore(cmToPixel(1f));
            table.setKeepTogether(true);

            createEventTitle(table, event);

            int position = 1;
            for (EventsRankingResult result : event.getResults()) {
                createAthleteRow(table, position, result);
                position++;
            }
            document.add(table);
        }
    }

    private void createEventTitle(PdfPTable table, EventsRankingEvent event) {
        addCategoryTitleCellWithColspan(table, event.getAbbreviation() + " / " + event.getGender(), 7);

        addCategoryTitleCellWithColspan(table, " ", 7);
    }

    private void createAthleteRow(PdfPTable table, int position, EventsRankingResult result) {
        addCell(table, position + ".");
        addCell(table, result.getLastName());
        addCell(table, result.getFirstName());
        addCell(table, result.getYearOfBirth() + "");
        addCell(table, result.getCategory());
        addCell(table, result.getClubId() == null ? "" : clubs.get(result.getClubId()));
        addCellAlignRight(table, "" + result.getResult());
    }

}
