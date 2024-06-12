package service.exception;

public class TaskNotFoundException extends Exception {

    public TaskNotFoundException(final String message) {
        super(message);
    }

    public TaskNotFoundException(final String message, Exception exception) {
        super(message, exception);
    }
}

