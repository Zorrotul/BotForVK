package vk.bot.error;

public class SendServiceException extends RuntimeException {

    public SendServiceException(Throwable cause) {
        super(cause);
    }

    public SendServiceException(String message) {
        super(message);
    }

}
