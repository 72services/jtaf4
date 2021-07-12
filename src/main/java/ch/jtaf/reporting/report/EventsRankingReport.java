package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.EventsRankingData;
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

public class EventsRankingReport extends RankingReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsRankingReport.class);

    private final EventsRankingData ranking;

    private Document document;

    public EventsRankingReport(EventsRankingData ranking, Locale locale) {
        super(locale);
        this.ranking = ranking;
    }

    public byte[] create() {
        try {
            byte[] ba;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                float border = cmToPixel(1.5f);
                document = new Document(A4, border, border, border, border);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
                pdfWriter.setPageEvent(new HeaderFooter(messages.getString("Event.Ranking"), ranking.name(),
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
        for (EventsRankingData.Event event : ranking.events()) {
            PdfPTable table = new PdfPTable(new float[]{2f, 10f, 10f, 2f, 2f, 5f, 5f});
            table.setWidthPercentage(100);
            table.setSpacingBefore(cmToPixel(1f));
            table.setKeepTogether(true);

            createEventTitle(table, event);

            int position = 1;
            for (EventsRankingData.Event.Result result : event.sortedResults()) {
                createAthleteRow(table, position, result);
                position++;
            }
            document.add(table);
        }
    }

    private void createEventTitle(PdfPTable table, EventsRankingData.Event event) {
        addCategoryTitleCellWithColspan(table, event.abbreviation() + " / " + event.gender(), 7);

        addCategoryTitleCellWithColspan(table, " ", 7);
    }

    private void createAthleteRow(PdfPTable table, int position, EventsRankingData.Event.Result result) {
        addCell(table, position + ".");
        addCell(table, result.lastName());
        addCell(table, result.firstName());
        addCell(table, result.yearOfBirth() + "");
        addCell(table, result.category());
        addCell(table, result.club() != null ? result.club() : "");
        addCellAlignRight(table, "" + result.result());
    }

}
