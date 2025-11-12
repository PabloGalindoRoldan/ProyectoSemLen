package ar.edu.unrn.seminario.exception;

/**
 * Runtime exception to represent persistence layer errors.
 * It logs the error to stderr in the constructor as requested.
 */
public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) {
        super(message);
        System.err.println("[PERSISTENCE ERROR] " + message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
        System.err.println("[PERSISTENCE ERROR] " + message + " - cause: " + cause);
        if (cause != null) cause.printStackTrace(System.err);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
        System.err.println("[PERSISTENCE ERROR] " + cause);
        if (cause != null) cause.printStackTrace(System.err);
    }
}
