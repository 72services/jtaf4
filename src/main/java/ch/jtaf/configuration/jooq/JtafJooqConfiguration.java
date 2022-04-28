package ch.jtaf.configuration.jooq;

import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JtafJooqConfiguration {

    @Bean
    Settings jooqSettings() {
        return new Settings().withRenderNameCase(RenderNameCase.LOWER).withRenderQuotedNames(RenderQuotedNames.NEVER);
    }
}
