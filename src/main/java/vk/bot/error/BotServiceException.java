package vk.bot.error;

public class BotServiceException extends RuntimeException {

    public BotServiceException(Throwable cause) {
        super(cause);
    }

    public BotServiceException(String message) {
        super(message);
    }


}
