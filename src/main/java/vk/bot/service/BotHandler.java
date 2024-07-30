package vk.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vk.bot.config.HandlerConfig;
import vk.bot.error.SendServiceException;
import vk.bot.model.client.HistoryDTO;
import vk.bot.model.client.VkMessageDTO;
import vk.bot.service.client.MessageHistoryServiceBean;
import vk.bot.service.client.SendService;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotHandler {

    private final HandlerConfig handlerConfig;
    private final SendService sendService;
    private final MessageHistoryServiceBean messageHistoryServiceBean;
    private Long lastMessageId;

    void handle() {
        HistoryDTO history = messageHistoryServiceBean.getHistory();
        handleUnprocessedMessages(history.getMessages());
    }

    private void handleUnprocessedMessages(List<VkMessageDTO> messages) {

        Long tempMaxId = messages.get(0).getId();

        Collections.reverse(messages);
        messages.stream()
                .filter(m -> !m.getFromId().equals(-sendService.getGroupId()))
                .filter(m -> m.getText() != null)
                .filter(m -> m.getId() > lastMessageId)
                .peek(m -> log.debug("message id: {}, from: {}, message: {}", m.getId(), m.getFromId(), m.getText()))
                .forEachOrdered(m -> tryToSendMessage(m.getText()));

        lastMessageId = tempMaxId;
    }

    public void init() {
        HistoryDTO history = messageHistoryServiceBean.getHistory();
        lastMessageId = history.getMessages().get(0).getId();
    }

    private void tryToSendMessage(String message) {
        for (int i = 0; i < handlerConfig.getNumberOfAttempts(); i++) {
            try {
                sendService.sendMessage(message);
                return;
            } catch (SendServiceException e) {
                try {
                    Thread.sleep(handlerConfig.getMessageSenderTimeout());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Thread was interrupted");
                    throw new RuntimeException(ie);
                }
            }
        }
    }
}
