package vk.bot.service;

import com.vk.api.sdk.objects.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vk.bot.model.client.HistoryDTO;
import vk.bot.service.client.VkBaseClient;
import vk.bot.model.client.VkMessageDTO;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BotHandler {

    private final BotService botService;
    private final VkBaseClient vkClient;
    private Integer ts;
    private Integer lastMessageId;
    private final static List<String> GREETINGS = Arrays.asList("Hello", "Hi", "Hey", "Howdy", "Даров", "Привет",
            "Здравствуй", "Добырый день");

    public BotHandler(BotService botService, VkBaseClient vkClient) {
        this.botService = botService;
        this.vkClient = vkClient;
        ts = botService.getNewTs();
    }

    void oldHandle() {
        List<Message> messages = botService.readMessage(ts);

        if (!messages.isEmpty()) {
            messages.forEach(message -> {
                log.info("get messages: {}", message.toString());
                if (GREETINGS.contains(message.getText())) {
                    botService.sendMessage(message.setText("И тебе привет!"));
                } else {
                    botService.sendMessage(message.setText("Вы написали: " + message.getText()));
                }
            });
        }
        ts = botService.getNewTs();
    }

    void handle() {
        HistoryDTO history = vkClient.getHistory();
        List<VkMessageDTO> newMessages = getUnprocessedMessages(history.getMessages());
    }

    private List<VkMessageDTO> getUnprocessedMessages(List<VkMessageDTO> messages){
        //List<VkMessage> messages = historyDTO.getMessages();

        List<VkMessageDTO> newMessages = messages.stream()
                .filter(m->!m.getFromId().equals(vkClient.getGroupId()))
                .filter(m->m.getText()!=null)
                .toList();
        return newMessages;
    }
}
