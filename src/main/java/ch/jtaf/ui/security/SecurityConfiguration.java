package ch.jtaf.ui.security;

import ch.jtaf.ui.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    public static final String LOGOUT_URL = "/";

    private final UserDetailsService userDetailsService;

    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        setLoginView(http, LoginView.class, LOGOUT_URL);

        http.userDetailsService(userDetailsService);

        http.rememberMe().key("7QPVkH83\\jA==BA`").alwaysRemember(true);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);

        web.ignoring().antMatchers("/icons/*.png");
        web.ignoring().antMatchers("/actuator/health");
    }
}
