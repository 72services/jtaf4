package ch.jtaf.ui.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails.getUsername();
        }
        // Anonymous or no authentication.
        return "";
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
