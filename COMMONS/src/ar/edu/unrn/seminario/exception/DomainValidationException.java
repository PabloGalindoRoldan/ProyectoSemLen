package ar.edu.unrn.seminario.exception;

/**
 * Runtime exception for domain (entity) validation failures.
 * Prints an error line to stderr when constructed.
 */
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
        System.err.println("[DOMAIN VALIDATION] " + message);
    }

    public DomainValidationException(String message, Throwable cause) {
        super(message, cause);
        System.err.println("[DOMAIN VALIDATION] " + message + " - cause: " + cause);
        if (cause != null) cause.printStackTrace(System.err);
    }
}
