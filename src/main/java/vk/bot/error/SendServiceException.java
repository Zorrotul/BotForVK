package vk.bot.error;

public class SendServiceException extends RuntimeException {

    private Long randomIdFromMessage;

    public SendServiceException(Throwable cause) {
        super(cause);
    }

    public SendServiceException(String message) {
        super(message);
    }

    public SendServiceException(String message, Long randomIdFromMessage) {
        super(message);
        this.randomIdFromMessage = randomIdFromMessage;
    }

    public Long getRandomIdFromMessage() {
        return randomIdFromMessage;
    }
}
