package vk.bot.error;

public class ErrorResponseException extends RuntimeException {

    public ErrorResponseException(Throwable cause) {
        super(cause);
    }

    public ErrorResponseException(String message) {
        super(message);
    }


}
