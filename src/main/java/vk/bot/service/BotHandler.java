package vk.bot.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BotHandler {

    private final BotService botService;
    private Integer ts;

    public BotHandler(BotService botService) {
        this.botService = botService;
        ts = botService.getNewTs();
    }

    void handle() {
        List<Message> messages = botService.readMessage(ts);

        if (!messages.isEmpty()) {
            messages.forEach(message -> {
                log.info("get messages: {}", message.toString());
                if (message.getText().equals("Ты жопа")) {
                    botService.sendMessage(message.setText("Сам ты жопа!"));
                } else {
                    botService.sendMessage(message.setText("Вы написали: " + message.getText()));
                }
            });
        }
    }
}
