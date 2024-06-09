package service.exception;

public class TaskIntersectionException extends RuntimeException {

        public TaskIntersectionException(final String message) {
            super(message);
        }

        public TaskIntersectionException(final String message, Exception exception) {
            super(message, exception);
        }
    }

