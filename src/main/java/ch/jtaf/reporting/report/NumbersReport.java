package ch.jtaf.reporting.report;

import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_MIDDLE;
import static com.lowagie.text.FontFactory.HELVETICA;
import static com.lowagie.text.PageSize.A4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import ch.jtaf.reporting.data.NumbersAndSheetsAthlete;

public class NumbersReport extends AbstractReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumbersReport.class);

    private static final float FONT_SIZE_INFO = 12f;
    private static final float FONT_SIZE_TEXT = 90f;

    private final List<NumbersAndSheetsAthlete> athletes;

    public NumbersReport(List<NumbersAndSheetsAthlete> athletes, Locale locale) {
        super(locale);
        this.athletes = athletes;
    }

    public byte[] create() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(A4, cmToPixel(1.5f), cmToPixel(1.7f), cmToPixel(0.8f), cmToPixel(0f));
            PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
            document.open();

            int i = 0;
            int number = 1;
            PdfPTable table = createMainTable();
            for (var athlete : athletes) {
                if (i > 9) {
                    document.add(table);
                    document.newPage();
                    i = 0;
                    table = createMainTable();
                }
                addAthleteInfo(table, athlete, number);
                if (i % 2 == 0) {
                    addEmptyCell(table);
                }
                if (i == 1 || i == 3 || i == 5 || i == 7) {
                    addEmptyRow(table);
                }
                i++;
                number++;
            }
            document.add(table);

            document.close();
            pdfWriter.flush();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    private PdfPTable createMainTable() {
        PdfPTable table = new PdfPTable(new float[]{10f, 1.8f, 10f});
        table.setWidthPercentage(100);
        return table;
    }

    private void addEmptyCell(PdfPTable table) {
        PdfPCell cellEmpty = new PdfPCell(new Phrase(""));
        cellEmpty.setBorder(0);
        table.addCell(cellEmpty);
    }

    private void addAthleteInfo(PdfPTable table, NumbersAndSheetsAthlete athlete, int number) {
        PdfPTable atable = new PdfPTable(1);
        atable.setWidthPercentage(100);

        PdfPCell cellId = new PdfPCell(
                new Phrase(number + "",
                        FontFactory.getFont(HELVETICA, FONT_SIZE_TEXT)));
        cellId.setBorder(0);
        cellId.setMinimumHeight(cmToPixel(2.5f));
        cellId.setHorizontalAlignment(ALIGN_CENTER);
        atable.addCell(cellId);

        String text = athlete.getLastName() + " " + athlete.getFirstName() + "\n";
        text += athlete.getCategory();
        if (athlete.getClub() != null) {
            text += " / " + athlete.getClub();
        }

        PdfPCell cellName = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, FONT_SIZE_INFO)));
        cellName.setBorder(0);
        cellName.setMinimumHeight(cmToPixel(1.8f));
        cellName.setHorizontalAlignment(ALIGN_CENTER);
        cellName.setVerticalAlignment(ALIGN_MIDDLE);
        atable.addCell(cellName);

        PdfPCell cellTable = new PdfPCell(atable);
        cellTable.setBorder(0);

        table.addCell(cellTable);
    }

    private void addEmptyRow(PdfPTable table) {
        PdfPCell cellId = new PdfPCell(new Phrase(" "));
        cellId.setBorder(0);
        cellId.setMinimumHeight(cmToPixel(0.5f));
        cellId.setHorizontalAlignment(ALIGN_CENTER);
        table.addCell(cellId);

        addEmptyCell(table);

        PdfPCell cellName = new PdfPCell(new Phrase(" "));
        cellName.setBorder(0);
        cellName.setMinimumHeight(cmToPixel(0.5f));
        cellName.setHorizontalAlignment(ALIGN_CENTER);
        table.addCell(cellName);
    }

}
