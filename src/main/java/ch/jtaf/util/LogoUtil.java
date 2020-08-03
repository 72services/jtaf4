package ch.jtaf.util;

import ch.jtaf.db.tables.records.SeriesRecord;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class LogoUtil {

    private LogoUtil() {
    }

    public static Image resizeLogo(SeriesRecord series) {
        Image logo = new Image();

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(series.getLogo()));
            double width = image.getWidth(null);
            double height = image.getHeight(null);
            double ratio = width / height;

            logo.setSrc(new StreamResource("logo", () -> new ByteArrayInputStream(series.getLogo())));
            logo.setHeight("60px");
            logo.setWidth(60 * ratio + "px");
        } catch (IOException e) {
            // Ignore
        }
        return logo;
    }
}
