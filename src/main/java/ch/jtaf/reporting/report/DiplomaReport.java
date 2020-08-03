package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.CompetitionRankingAthlete;
import ch.jtaf.reporting.data.CompetitionRankingCategory;
import ch.jtaf.reporting.data.CompetitionRankingData;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.FontFactory.HELVETICA;
import static com.lowagie.text.PageSize.A5;

public class DiplomaReport extends AbstractReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiplomaReport.class);

    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d. MMMM yyyy");
    private final CompetitionRankingData ranking;
    private final byte[] logo;

    private Document document;

    public DiplomaReport(CompetitionRankingData competitionRankingData, byte[] logo, Locale locale) {
        super(locale);
        this.ranking = competitionRankingData;
        this.logo = logo;
    }

    public byte[] create() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            document = new Document(A5, cmToPixel(1.5f), cmToPixel(1.5f), cmToPixel(1f), cmToPixel(1.5f));
            var pdfWriter = PdfWriter.getInstance(document, baos);
            document.open();

            for (var category : ranking.getCategories()) {
                for (var athlete : category.getAthletes()) {
                    createTitle();
                    createLogo();
                    createCompetitionInfo();
                    createAthleteInfo(athlete, category);

                    document.newPage();
                }
            }

            document.close();
            pdfWriter.flush();
            return baos.toByteArray();
        } catch (IOException | DocumentException e) {
            LOGGER.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    private void createLogo() throws DocumentException {
        if (logo != null) {
            try {
                var image = Image.getInstance(logo);
                image.scaleToFit(cmToPixel(11f), cmToPixel(11f));
                image.setAbsolutePosition((cmToPixel(14.85f) - image.getScaledWidth()) / 2,
                    (cmToPixel(11f) - image.getScaledHeight()) / 2 + cmToPixel(5.5f));
                document.add(image);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void createAthleteInfo(CompetitionRankingAthlete athlete, CompetitionRankingCategory category) throws DocumentException {
        var table = new PdfPTable(new float[]{2f, 10f, 10f, 3f, 2f});
        table.setWidthPercentage(100f);
        table.setSpacingBefore(cmToPixel(1.5f));

        float athleteFontSize = 12f;
        addCell(table, athlete.getRank() + ".", athleteFontSize);
        addCell(table, athlete.getLastName(), athleteFontSize);
        addCell(table, athlete.getFirstName(), athleteFontSize);
        addCell(table, "" + athlete.getYearOfBirth(), athleteFontSize);
        addCell(table, category.getAbbreviation(), athleteFontSize);

        document.add(table);
    }

    private void createTitle() throws DocumentException {
        var table = new PdfPTable(1);
        table.setWidthPercentage(100f);

        var cell = new PdfPCell(new Phrase(messages.getString("Diploma"), FontFactory.getFont(HELVETICA, 60f)));
        cell.setBorder(0);
        cell.setHorizontalAlignment(ALIGN_CENTER);

        table.addCell(cell);
        document.add(table);
    }

    private void createCompetitionInfo() throws DocumentException {
        var table = new PdfPTable(1);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(cmToPixel(12f));

        var cell = new PdfPCell(new Phrase(ranking.getName(), FontFactory.getFont(HELVETICA, 25f)));
        cell.setBorder(0);
        cell.setHorizontalAlignment(ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(DATE_FORMATTER.format(ranking.getCompetitionDate()), FontFactory.getFont(HELVETICA, 25f)));
        cell.setBorder(0);
        cell.setHorizontalAlignment(ALIGN_CENTER);
        table.addCell(cell);

        document.add(table);
    }
}
