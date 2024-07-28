package vk.bot.error;

public class HandlerException extends RuntimeException {

    public HandlerException(Throwable cause) {
        super(cause);
    }

    public HandlerException(String message) {
        super(message);
    }


}
