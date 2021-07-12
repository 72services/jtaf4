package ch.jtaf.reporting.report;

import ch.jtaf.model.EventType;
import ch.jtaf.reporting.data.NumbersAndSheetsAthlete;
import ch.jtaf.reporting.data.NumbersAndSheetsCompetition;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.lowagie.text.Element.ALIGN_RIGHT;
import static com.lowagie.text.PageSize.A5;

public class SheetsReport extends AbstractReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(SheetsReport.class);

    private static final float INFO_LINE_HEIGHT = 40f;
    private static final float FONT_SIZE_INFO = 8f;
    private static final float FONT_SIZE_TEXT = 16f;

    private Document document;
    private PdfWriter pdfWriter;
    private final NumbersAndSheetsCompetition competition;
    private final List<NumbersAndSheetsAthlete> athletes;
    private final byte[] logo;

    public SheetsReport(NumbersAndSheetsAthlete athlete, byte[] logo, Locale locale) {
        super(locale);
        this.competition = null;
        this.athletes = new ArrayList<>();
        this.athletes.add(athlete);
        this.logo = logo;
    }

    public SheetsReport(NumbersAndSheetsCompetition competition, NumbersAndSheetsAthlete athlete, byte[] logo, Locale locale) {
        super(locale);
        this.competition = competition;
        this.athletes = new ArrayList<>();
        this.athletes.add(athlete);
        this.logo = logo;
    }

    public SheetsReport(NumbersAndSheetsCompetition competition, List<NumbersAndSheetsAthlete> athletes, byte[] logo, Locale locale) {
        super(locale);
        this.competition = competition;
        this.athletes = athletes;
        this.logo = logo;
    }

    public byte[] create() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            float oneCm = cmToPixel(1f);
            document = new Document(A5, oneCm, oneCm, cmToPixel(4.5f), oneCm);
            pdfWriter = PdfWriter.getInstance(document, baos);
            document.open();
            boolean first = true;
            int number = 1;
            for (var athlete : athletes) {
                if (!first) {
                    document.newPage();
                }
                createLogo();
                createCategory(athlete);
                createAthleteInfo(athlete, number);
                createCompetitionRow();
                createEventTable(athlete);
                first = false;
                number++;
            }
            document.close();
            pdfWriter.flush();
            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            LOGGER.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    private void createLogo() throws DocumentException {
        if (logo != null) {
            try {
                Image image = Image.getInstance(logo);
                image.setAbsolutePosition(cmToPixel(1f), cmToPixel(17.5f));
                image.scaleToFit(120, 60);
                document.add(image);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void createCategory(NumbersAndSheetsAthlete athlete) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        addCategoryCell(table, athlete.category());

        Rectangle page = document.getPageSize();
        table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
        table.writeSelectedRows(0, 1, document.leftMargin(), cmToPixel(20.5f), pdfWriter.getDirectContent());
    }

    private void createAthleteInfo(NumbersAndSheetsAthlete athlete, int number) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(cmToPixel(1f));

        if (athlete.id() != null) {
            addInfoCell(table, "" + number);
            addCell(table, athlete.id().toString());
        } else {
            addCell(table, " ");
            addCell(table, " ");
        }
        if (athlete.lastName() == null) {
            addInfoCellWithBorder(table, messages.getString("Last.Name"));
        } else {
            addInfoCell(table, athlete.lastName());
        }
        if (athlete.firstName() == null) {
            addInfoCellWithBorder(table, messages.getString("First.Name"));
        } else {
            addInfoCell(table, athlete.firstName());
        }
        if (athlete.yearOfBirth() == null) {
            addInfoCellWithBorder(table, messages.getString("Year"));
        } else {
            addInfoCell(table, athlete.yearOfBirth() + "");
        }
        if (athlete.club() == null) {
            if (athlete.id() == null) {
                addInfoCellWithBorder(table, messages.getString("Club"));
            } else {
                addInfoCell(table, "");
            }
        } else {
            addInfoCell(table, athlete.club());
        }

        document.add(table);
    }

    private void createCompetitionRow() throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(cmToPixel(0.5f));
        table.setSpacingAfter(cmToPixel(0.5f));

        addCompetitionCell(table, competition == null ? "" : competition.name() + " " + DATE_TIME_FORMATTER.format(competition.competitionDate()));

        document.add(table);
    }

    @SuppressWarnings("IfStatementWithIdenticalBranches")
    private void createEventTable(NumbersAndSheetsAthlete athlete) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(cmToPixel(1f));

        for (var event : athlete.events()) {
            if (event.type().equals(EventType.JUMP_THROW.name())) {
                addInfoCell(table, event.name());
                addInfoCellWithBorder(table, "");
                addInfoCellWithBorder(table, "");
                addInfoCellWithBorder(table, "");
            } else {
                addInfoCellWithColspan(table, event.name(), 3);
                addInfoCellWithBorder(table, "");
            }
        }

        document.add(table);
    }

    protected void addCategoryCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
            new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 80f)));
        cell.setBorder(0);
        cell.setHorizontalAlignment(ALIGN_RIGHT);
        table.addCell(cell);
    }

    private void addCompetitionCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
            new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, FONT_SIZE_TEXT)));
        cell.setBorder(0);
        table.addCell(cell);
    }

    private void addInfoCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
            new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_TEXT)));
        cell.setBorder(0);
        cell.setMinimumHeight(INFO_LINE_HEIGHT);
        table.addCell(cell);
    }

    private void addInfoCellWithColspan(PdfPTable table, String text, int colspan) {
        PdfPCell cell = new PdfPCell(
            new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_TEXT)));
        cell.setBorder(0);
        cell.setColspan(colspan);
        cell.setMinimumHeight(INFO_LINE_HEIGHT);
        table.addCell(cell);
    }

    private void addInfoCellWithBorder(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(
            new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, FONT_SIZE_INFO)));
        cell.setMinimumHeight(INFO_LINE_HEIGHT);
        cell.setBorderWidth(1);
        table.addCell(cell);
    }
}
