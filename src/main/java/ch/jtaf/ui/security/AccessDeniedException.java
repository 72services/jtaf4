package ch.jtaf.ui.security;

import java.io.Serial;

public class AccessDeniedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public AccessDeniedException() {
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
