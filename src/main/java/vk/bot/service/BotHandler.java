package vk.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import vk.bot.config.HandlerConfig;
import vk.bot.error.HandlerException;
import vk.bot.error.SendServiceException;
import vk.bot.error.VKResponseException;
import vk.bot.model.client.ErrorRequestParamsDTO;
import vk.bot.model.client.HistoryDTO;
import vk.bot.model.client.ResponseSendMessageDTO;
import vk.bot.model.client.VkMessageDTO;
import vk.bot.service.client.MessageHistoryServiceBean;
import vk.bot.service.client.SendService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("BusyWait")
@Slf4j
@Service
@RequiredArgsConstructor
public class BotHandler {

    private final HandlerConfig handlerConfig;
    private final SendService sendService;
    private final MessageHistoryServiceBean messageHistoryServiceBean;
    private Long lastMessageId;

    void handle() {
        HistoryDTO history = getHistory();
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
        HistoryDTO history = getHistory();
        lastMessageId = history.getMessages().get(0).getId();
        tryToSendMessage("bot started");
    }

    private HistoryDTO getHistory() {
        for (int i = 0; i < handlerConfig.getNumberOfAttempts(); i++) {
            try {
                return messageHistoryServiceBean.getHistory();
            } catch (RestClientResponseException e) {
                if (e.getStatusCode().is4xxClientError()) {
                    throw new HandlerException(e.getStatusCode().toString());
                }
                log.info("try to get history {} time", i + 1, e);
                try {
                    Thread.sleep(handlerConfig.getWaitingTimeout());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Thread was interrupted");
                    throw new RuntimeException(ie);
                }
            }
        }
        throw new HandlerException("Cant get history");
    }

    private void tryToSendMessage(String message) {

        try {
            processSendResponse(sendService.sendMessage(message));
        } catch (VKResponseException e) {
            tryToResendMessage(message, e.getRandomIdFromMessage());
        } catch (SendServiceException e) {
            log.error("tryToSendMessage<-", e);
        }
    }

    private void tryToResendMessage(String message, Long randomId) {
        for (int i = 1; i < handlerConfig.getNumberOfAttempts(); i++) {
            try {
                try {
                    Thread.sleep(handlerConfig.getWaitingTimeout());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Thread was interrupted");
                    throw new RuntimeException(ie);
                }
                processSendResponse(sendService.sendMessage(message, randomId));
                return;
            } catch (VKResponseException e) {
                log.info("{} try to send message {}", i + 1, message);
            }
        }
    }

    private void processSendResponse(ResponseEntity<ResponseSendMessageDTO> response) {

        Optional.ofNullable(response)
                .map(HttpEntity::getBody)
                .map(ResponseSendMessageDTO::getError)
                .ifPresent(error -> {
                    ErrorRequestParamsDTO randomId = error.getRequestParams().stream()
                            .filter(r -> r.getKey().equals("random_id"))
                            .findFirst()
                            .orElseThrow(() -> new SendServiceException("No random id in response"));
                    throw new VKResponseException("Message was not delivered, cause: " + response.getBody().getError().toString(), Long.valueOf(randomId.getValue()));
                });

    }
}
