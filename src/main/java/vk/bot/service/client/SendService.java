package vk.bot.service.client;

import org.springframework.http.ResponseEntity;
import vk.bot.model.client.ResponseSendMessageDTO;

public interface SendService {
    ResponseEntity<ResponseSendMessageDTO> sendMessage(String message);

    ResponseEntity<ResponseSendMessageDTO> sendMessage(String message, Long randomId);

    Long getGroupId();
}
