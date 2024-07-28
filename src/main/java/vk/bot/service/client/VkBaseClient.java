package vk.bot.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vk.bot.config.BotCreedsConfig;
import vk.bot.error.BotServiceException;
import vk.bot.model.client.HistoryDTO;
import vk.bot.model.client.ResponseHistoryWrapperDTO;

import java.util.Optional;

@Slf4j
@Service
public class VkBaseClient {

    private final RestTemplate restTemplate;

    private final String accessToken;
    private final Long peerId;
    private final Long groupId;
    private final BotCreedsConfig creeds;

    private final String sendUrl = "https://api.vk.com/method/messages.send";
    private final String getHistoryUrl = "https://api.vk.com/method/messages.getHistory";

    public VkBaseClient(
            RestTemplate restTemplate, BotCreedsConfig creeds) {
        this.restTemplate = restTemplate;

        log.info("VkBaseConstructor<-");
// create config
        this.creeds = creeds;
        this.accessToken = creeds.getGroupToken();
        log.info("Token: {}", accessToken);
        this.peerId = 161736917L;
        this.groupId = 226661740L;
    }


    public void sendMessage(String message) {
        log.info("sendMessage<-");
        try {
            restTemplate.postForEntity(sendUrl + "?v=5.199&access_token=" + accessToken + "&group_id=226661740&user_id=161736917&message=" + message + "&random_id=0", null, String.class);
        } catch (Exception e) {
            log.error("Cant send message: {}", message, e);
        }
    }

//    public HistoryDTO getHistory() {
//        log.info("getHistory<-");
//
//        String url = UriComponentsBuilder.fromHttpUrl(getHistoryUrl)
//                .queryParam("v", "5.199")
//                .queryParam("access_token", accessToken)
//                .queryParam("count", 200)
//                .queryParam("offset", 0)
//                .queryParam("peer_id", peerId)
//                .queryParam("group_id", groupId)
//                .queryParam("rev", 0)
//                //.queryParam("start_message_id ", -1)
//                .toUriString();
//
//        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
//        return Optional.of(response)
//                .filter(r -> {
//                    log.info("status: {}", r.getStatusCode());
//                    return r.getStatusCode().is2xxSuccessful();
//                })
//                .map(ResponseEntity::getBody)
//                .map(body -> {
//                    log.info("body: {}", body);
//                    ResponseHistoryWrapper responseHistoryWrapper = null;
//                    try {
//                        responseHistoryWrapper = objectMapper.readValue(body, ResponseHistoryWrapper.class);
//                    } catch (JsonProcessingException e) {
//                        log.error("Cant deserialize response");
//                        throw new VkClientException(e);
//                    }
//                    log.info(responseHistoryWrapper.toString());
//                    HistoryDTO history = responseHistoryWrapper.getResponse();
//                    log.info(history.toString());
//                    return history;
//                })
//                .orElseThrow(() -> {
//                    log.error("Failed to fetch history");
//                    throw new BotServiceException("");
//                });
//    }

    public HistoryDTO getHistory() {
        log.info("getHistory<-");

        String url = UriComponentsBuilder.fromHttpUrl(getHistoryUrl)
                .queryParam("v", "5.199")
                .queryParam("access_token", accessToken)
                .queryParam("count", 200)
                .queryParam("offset", 0)
                .queryParam("peer_id", peerId)
                .queryParam("group_id", groupId)
                .queryParam("rev", 0)
                //.queryParam("start_message_id ", -1)
                .toUriString();

        ResponseEntity<ResponseHistoryWrapperDTO> response = restTemplate.getForEntity(url, ResponseHistoryWrapperDTO.class);
        return Optional.of(response)
                .filter(r -> {
                    log.info("status: {}", r.getStatusCode());
                    return r.getStatusCode().is2xxSuccessful();
                })
                .map(ResponseEntity::getBody)
                .map(body -> {
                    log.info("body: {}", body);
                    ResponseHistoryWrapperDTO responseHistoryWrapper = response.getBody();
                    log.info(responseHistoryWrapper.toString());
                    HistoryDTO history = responseHistoryWrapper.getResponse();
                    log.info(history.toString());
                    return history;
                })
                .orElseThrow(() -> {
                    log.error("Failed to fetch history");
                    throw new BotServiceException("");
                });
    }

    public String getHistoryAsString() {
        log.info("getHistory<-");

        String url = UriComponentsBuilder.fromHttpUrl(getHistoryUrl)
                .queryParam("v", "5.199")
                .queryParam("access_token", accessToken)
                .queryParam("count", 200)
                .queryParam("offset", 0)
                .queryParam("peer_id", peerId)
                .queryParam("group_id", groupId)
                .toUriString();

//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("v", "5.199");
//        params.add("access_token", accessToken);
//        params.add("count", String.valueOf(10));
//        params.add("offset", String.valueOf(20));
//        params.add("peer_id", String.valueOf(peer_id));
//        params.add("group_id", String.valueOf(group_id));

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            return Optional.of(response)
                    .filter(r -> {
                        r.getStatusCode().is2xxSuccessful();
                        log.info("status: {}", r.getStatusCode());
                        log.info("body: {}", r.getBody());
                        return r.hasBody();
                    })
                    .map(ResponseEntity::getBody)
                    .map(body -> {
                        log.info("body: " + body);
                        return body;
                    })
                    .orElseThrow(() -> {
                        log.error("Failed to fetch history");
                        throw new BotServiceException("");
                    });
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public Long getGroupId() {
        return groupId;
    }
}
