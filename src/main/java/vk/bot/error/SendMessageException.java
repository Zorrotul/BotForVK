package vk.bot.error;

public class SendMessageException extends RuntimeException{

    public SendMessageException(Throwable cause) {
        super(cause);
    }

    public SendMessageException(String message) {
        super(message);
    }



}
