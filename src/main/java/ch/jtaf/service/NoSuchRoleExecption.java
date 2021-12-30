package ch.jtaf.service;

public class NoSuchRoleExecption extends Exception {

    private final String missingRole;

    public NoSuchRoleExecption(String role) {
        super();
        this.missingRole = role;
    }

    public String getMissingRole() {
        return missingRole;
    }
}
