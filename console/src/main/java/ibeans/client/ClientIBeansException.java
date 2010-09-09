package ibeans.client;

/**
 *
 */
public class ClientIBeansException extends Exception {

    public ClientIBeansException() {
    }

    public ClientIBeansException(Throwable cause) {
        super(cause);
    }

    public ClientIBeansException(String message) {
        super(message);
    }

    public ClientIBeansException(String message, Throwable cause) {
        super(message, cause);
    }
}