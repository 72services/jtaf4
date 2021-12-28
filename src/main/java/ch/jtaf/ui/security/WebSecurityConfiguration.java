package ch.jtaf.ui.security;

import ch.jtaf.security.UserDetailsServiceImpl;
import com.vaadin.flow.spring.security.RequestUtil;
import com.vaadin.flow.spring.security.VaadinDefaultRequestCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter.getDefaultHttpSecurityPermitMatcher;
import static com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter.getDefaultWebSecurityIgnoreMatcher;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form,</li>
 * <li>Configures the {@link UserDetailsServiceImpl}.</li>
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGIN_URL = "/login";
    private static final String LOGIN_SUCCESS_URL = "/organizations";
    private static final String LOGOUT_SUCCESS_URL = "/";

    private final UserDetailsService userDetailsService;
    private final RequestUtil requestUtil;
    private final VaadinDefaultRequestCache vaadinDefaultRequestCache;

    public WebSecurityConfiguration(UserDetailsService userDetailsService, RequestUtil requestUtil,
                                    VaadinDefaultRequestCache vaadinDefaultRequestCache) {
        this.userDetailsService = userDetailsService;
        this.requestUtil = requestUtil;
        this.vaadinDefaultRequestCache = vaadinDefaultRequestCache;
    }

    /**
     * The password encoder to use when encrypting passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Registers our UserDetailsService and the password encoder to be used on login attempts.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * Require login to access internal pages and configure login form.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()

            .requestCache().requestCache(vaadinDefaultRequestCache)

            // Restrict access to our application.
            .and().authorizeRequests()

            // Allow all flow internal requests.
            .requestMatchers(requestUtil::isFrameworkInternalRequest).permitAll()
            .requestMatchers(requestUtil::isAnonymousRoute).permitAll()
            .requestMatchers(getDefaultHttpSecurityPermitMatcher()).permitAll()

            // Allow all requests by logged-in users.
            .anyRequest().authenticated()

            // Configure the login page.
            .and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL).successForwardUrl(LOGIN_SUCCESS_URL).failureUrl(LOGIN_FAILURE_URL)

            // Configure logout
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)

            // Remember Me
            .and().rememberMe().key("7QPVkH83\\jA==BA`").alwaysRemember(true);
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(getDefaultWebSecurityIgnoreMatcher());

        web.ignoring().antMatchers("/icons/*.png");
        web.ignoring().antMatchers("/actuator/health");
    }
}
