package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.ClubResultData;
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

public class ClubRankingReport extends RankingReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClubRankingReport.class);

    private final ClubRankingData ranking;
    private Document document;

    public ClubRankingReport(ClubRankingData ranking, Locale locale, Map<Long, String> clubs) {
        super(locale, clubs);
        this.ranking = ranking;
    }

    public byte[] create() {
        try {
            try (var baos = new ByteArrayOutputStream()) {
                var border = cmToPixel(1.5f);
                document = new Document(A4, border, border, border, border);

                var pdfWriter = PdfWriter.getInstance(document, baos);
                pdfWriter.setPageEvent(new HeaderFooter(messages.getString("club.ranking"), ranking.getSeriesName(), ""));

                document.open();

                createRanking();

                document.close();

                pdfWriter.flush();
                return baos.toByteArray();
            }
        } catch (IOException | DocumentException e) {
            LOGGER.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    private void createRanking() throws DocumentException {
        var table = new PdfPTable(new float[]{2f, 10f, 10f});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(1f);

        ranking.getResults().forEach(result -> createClubRow(table, result));

        document.add(table);
    }

    private void createClubRow(PdfPTable table, ClubResultData clubResultData) {
        addCell(table, clubResultData.getRank() + ".");
        addCell(table, clubResultData.getClub());
        addCellAlignRight(table, "" + clubResultData.getPoints());
    }

}
