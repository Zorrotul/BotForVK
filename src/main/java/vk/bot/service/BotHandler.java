package vk.bot.service;

import com.vk.api.sdk.objects.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class BotHandler {

    private final BotService botService;
    private Integer ts;
    private final static List<String> GREETINGS = Arrays.asList("Hello", "Hi", "Hey", "Howdy", "Даров", "Привет",
            "Здравствуй", "Добырый день");

    public BotHandler(BotService botService) {
        this.botService = botService;
        ts = botService.getNewTs();
    }

    void handle() {
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
}
