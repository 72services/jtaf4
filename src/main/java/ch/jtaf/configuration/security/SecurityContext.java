package ch.jtaf.configuration.security;

import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import static ch.jtaf.context.ApplicationContextHolder.getBean;

/**
 * Takes care of all such static operations that have to do with security and
 * querying rights from different beans of the UI.
 */
public final class SecurityContext {

    private SecurityContext() {
        // Util methods only
    }

    /**
     * Gets the username of the currently signed-in user.
     *
     * @return the username of the current user or <code>null</code> if the user
     * has not signed in
     */
    public static String getUsername() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof Jwt jwt) {
            return jwt.getSubject();
        } else {
            // Anonymous or no authentication.
            return "";
        }
    }

    /**
     * Checks if the user is logged in.
     *
     * @return true if the user is logged in. False otherwise.
     */
    public static boolean isUserLoggedIn() {
        return isUserLoggedIn(SecurityContextHolder.getContext().getAuthentication());
    }

    private static boolean isUserLoggedIn(Authentication authentication) {
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static void logout() {
        var request = VaadinServletRequest.getCurrent().getHttpServletRequest();

        getBean(AuthenticationContext.class).logout();

        var cookieName = "remember-me";
        var cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");

        var response = (HttpServletResponse) VaadinResponse.getCurrent();
        response.addCookie(cookie);
    }
}
