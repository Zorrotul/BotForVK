package vk.bot.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import vk.bot.config.BotCreedsConfig;
import vk.bot.config.ClientConfig;
import vk.bot.error.SendServiceException;

@Slf4j
@Service
public class SendServiceBean implements SendService {

    private final RestTemplate restTemplate;
    private final BotCreedsConfig creeds;
    private final ClientConfig clientConfig;
    private final String sendUrl = "https://api.vk.com/method/messages.send";
    private final HttpHeaders headers;

    public SendServiceBean(RestTemplate restTemplate, BotCreedsConfig creeds, ClientConfig clientConfig) {
        this.restTemplate = restTemplate;
        this.creeds = creeds;
        this.clientConfig = clientConfig;
        this.headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
    }

    @Override
    public void sendMessage(String message) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("v", "5.199");
        body.add("access_token", creeds.getGroupToken());
        body.add("peer_id", String.valueOf(clientConfig.getPeerId()));
        body.add("message", String.format("Вы сказали: %s", message));
        body.add("group_id", String.valueOf(creeds.getGroupId()));
        body.add("random_id", "0");

        log.info("sendMessage<-");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        log.info("request: {}", request);

        try {
            restTemplate.postForEntity(sendUrl, request, String.class);
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
