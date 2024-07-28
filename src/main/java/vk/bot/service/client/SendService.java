package vk.bot.service.client;

public interface SendService {
    void sendMessage(String message);

    Long getGroupId();
}
