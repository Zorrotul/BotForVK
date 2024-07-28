package vk.bot.error;

public class VkClientException extends RuntimeException {

    public VkClientException(Throwable cause) {
        super(cause);
    }

    public VkClientException(String message) {
        super(message);
    }


}
