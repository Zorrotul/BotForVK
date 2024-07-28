package vk.bot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vk.bot.model.client.HistoryDTO;
import vk.bot.service.client.VkBaseClient;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendServiceBean implements SendService {

    private final VkBaseClient vkBaseClient;

    @PostConstruct
    public void init() {
        log.info("init<-");
        getHistory();
    }

    public void sendMessage(String message) {
        vkBaseClient.sendMessage(message);
    }

    public HistoryDTO getHistory() {
        HistoryDTO historyDTO = vkBaseClient.getHistory();
        log.info(historyDTO.toString());
        historyDTO.getMessages().stream()
                .peek(m -> log.info("message id: {}, from: {}, message: {}", m.getId(), m.getFromId(), m.getText()))
                .collect(Collectors.toList());

        return historyDTO;
    }

}