package ch.jtaf.reporting.report;

import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.Locale;
import java.util.Map;

import static com.itextpdf.text.FontFactory.HELVETICA;
import static com.itextpdf.text.FontFactory.HELVETICA_BOLD;

public class RankingReport extends AbstractReport {

    final Map<Long, String> clubs;

    RankingReport(Locale locale, Map<Long, String> clubs) {
        super(locale);
        this.clubs = clubs;
    }

    void addCategoryTitleCellWithColspan(PdfPTable table, String text, int colspan) {
        var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA_BOLD, 12f)));
        cell.setBorder(0);
        cell.setColspan(colspan);
        table.addCell(cell);
    }

    void addResultsCell(PdfPTable table, String text) {
        var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, 7f)));
        cell.setColspan(5);
        cell.setBorder(0);
        cell.setPaddingBottom(8f);
        table.addCell(cell);
    }
}
