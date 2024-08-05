package vk.bot.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import vk.bot.config.BotCreedsConfig;
import vk.bot.config.ClientConfig;
import vk.bot.error.SendServiceException;
import vk.bot.model.client.ResponseSendMessageDTO;

@Slf4j
@Service
public class SendServiceBean implements SendService {

    private final RestTemplate restTemplate;
    private final BotCreedsConfig creeds;
    private final HttpHeaders headers;
    private final MultiValueMap<String, String> urlBody;
    private final String sendUrl;
    private final ClientConfig clientConfig;


    public SendServiceBean(RestTemplate restTemplate, BotCreedsConfig creeds, ClientConfig clientConfig) {
        this.restTemplate = restTemplate;
        this.creeds = creeds;
        this.clientConfig = clientConfig;
        this.headers = new HttpHeaders();
        this.urlBody = new LinkedMultiValueMap<>();

        this.headers.set("Content-Type", "application/x-www-form-urlencoded");
        this.sendUrl = clientConfig.getUrl() + clientConfig.getSendMessageMethod();
        this.urlBody.add("v", clientConfig.getVersionApi());
        this.urlBody.add("access_token", creeds.getAccessToken());
        this.urlBody.add("peer_id", String.valueOf(clientConfig.getPeerId()));
        this.urlBody.add("group_id", String.valueOf(creeds.getGroupId()));
    }

    @Override
    public ResponseEntity<ResponseSendMessageDTO> sendMessage(String message) {
        log.debug("sendMessage<-");

        this.urlBody.set("message", String.format("Вы сказали: %s", message));
        this.urlBody.set("random_id", String.valueOf(clientConfig.getRandomId()));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(urlBody, headers);
        log.info("request: {}", request);

        try {
            return restTemplate.postForEntity(sendUrl, request, ResponseSendMessageDTO.class);

        } catch (RestClientException e) {
            log.error("Cant send message: {}", message, e);
            throw new SendServiceException(e);
        }
    }

    @Override
    public ResponseEntity<ResponseSendMessageDTO> sendMessage(String message, Long randomId) {
        log.info("sendMessage with random id<-");

        this.urlBody.set("message", String.format("Вы сказали: %s", message));
        this.urlBody.set("random_id", String.valueOf(randomId));


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(urlBody, headers);
        log.info("request: {}", request);

        try {
            return restTemplate.postForEntity(sendUrl, request, ResponseSendMessageDTO.class);

        } catch (RestClientException e) {
            log.error("Cant send message: {}", message, e);
            throw new SendServiceException(e);
        }
    }

    @Override
    public Long getGroupId() {
        return creeds.getGroupId();
    }
}
