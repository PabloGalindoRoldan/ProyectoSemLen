package ar.edu.unrn.seminario.exception;

public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
        System.err.println("[VALIDACION] " + message);
    }

    public DomainValidationException(String message, Throwable cause) {
        super(message, cause);
        System.err.println("[VALIDACION] " + message + " - cause: " + cause);
        if (cause != null) cause.printStackTrace(System.err);
    }
}
