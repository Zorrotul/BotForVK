package vk.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vk.bot.config.HandlerConfig;
import vk.bot.error.ErrorResponseException;
import vk.bot.error.SendServiceException;
import vk.bot.model.client.ErrorRequestParamsDTO;
import vk.bot.model.client.HistoryDTO;
import vk.bot.model.client.ResponseSendMessageDTO;
import vk.bot.model.client.VkMessageDTO;
import vk.bot.service.client.MessageHistoryServiceBean;
import vk.bot.service.client.SendService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        ResponseEntity<ResponseSendMessageDTO> response = sendService.sendMessage("bot started");
        log.info("First message response: {}", String.valueOf(response));
    }

    private void tryToSendMessage(String message) {

        try {
            processSendResponse(sendService.sendMessage(message));
        } catch (SendServiceException e) {
            tryToResendMessage(message, e.getRandomIdFromMessage());
        }

    }

    private void tryToResendMessage(String message, Long randomId) {
        for (int i = 0; i < handlerConfig.getNumberOfAttempts() - 1; i++) {
            try {
                processSendResponse(sendService.sendMessage(message, randomId));
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

    private void processSendResponse(ResponseEntity<ResponseSendMessageDTO> response) {
        if (Objects.requireNonNull(response.getBody()).getResponse() != null) {
            log.info("Response code: {}", response.getBody().getResponse().getCode().toString());
        } else if (response.getBody().getError() != null) {
            ErrorRequestParamsDTO randomId = response.getBody().getError().getRequestParams().stream()
                    .filter(r -> r.getKey().equals("random_id"))
                    .findFirst()
                    .orElseThrow(() -> new ErrorResponseException("No random id in response"));
            throw new SendServiceException("Message was not send? cause: " + response.getBody().getError().toString(), Long.valueOf(randomId.getValue()));
        } else {
            throw new SendServiceException("Unknown response");
        }
    }
}
