package ch.jtaf;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics;

@EnableGoogleAnalytics("G-PH4RL4J6YT")
@Theme("jtaf")
@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
@StyleSheet("https://fonts.googleapis.com/css2?family=Poppins")
@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
public class Jtaf4Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Jtaf4Application.class, args);
    }

}
