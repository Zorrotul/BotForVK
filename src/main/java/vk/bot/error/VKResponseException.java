package vk.bot.error;

public class VKResponseException extends RuntimeException {

    private final Long randomIdFromMessage;

    public VKResponseException(Throwable cause, Long randomIdFromMessage) {
        super(cause);
        this.randomIdFromMessage = randomIdFromMessage;
    }

    public VKResponseException(String message, Long randomIdFromMessage) {
        super(message);
        this.randomIdFromMessage = randomIdFromMessage;
    }

    public Long getRandomIdFromMessage() {
        return randomIdFromMessage;
    }

}
