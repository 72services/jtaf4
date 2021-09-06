package ch.jtaf;

import com.vaadin.flow.component.dependency.NpmPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

@NpmPackage(value = "lumo-css-framework", version = "^4.0.10")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@SpringBootApplication(exclude = {R2dbcAutoConfiguration.class})
public class Jtaf4Application {

    public static void main(String[] args) {
        SpringApplication.run(Jtaf4Application.class, args);
    }

}
