package ch.jtaf.ui.security;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.RolesAllowed;
import java.util.Arrays;
import java.util.List;

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
    @SuppressWarnings("unused")
    public static String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
            return userDetails.getUsername();
        }
        // Anonymous or no authentication.
        return null;
    }

    /**
     * Checks if access is granted for the current user for the given secured view,
     * defined by the view class.
     *
     * @param securedClass View class
     * @return true if access is granted, false otherwise.
     */
    public static boolean isAccessGranted(Class<?> securedClass) {
        AnonymousAllowed anonymousAllowed = AnnotationUtils.findAnnotation(securedClass, AnonymousAllowed.class);
        if (anonymousAllowed != null) {
            return true;
        }

        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();

        // All other views require authentication
        if (!isUserLoggedIn(userAuthentication)) {
            return false;
        }

        // Allow if no roles are required.
        RolesAllowed rolesAllowed = AnnotationUtils.findAnnotation(securedClass, RolesAllowed.class);
        if (rolesAllowed == null) {
            return true;
        }

        List<String> allowedRoles = Arrays.asList(rolesAllowed.value());
        return userAuthentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(allowedRoles::contains);
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

}
