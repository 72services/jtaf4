package ch.jtaf.reporting.report;

import static com.lowagie.text.FontFactory.HELVETICA;
import static com.lowagie.text.FontFactory.HELVETICA_BOLD;

import java.util.Locale;
import java.util.Map;

import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

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
