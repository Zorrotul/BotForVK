package vk.bot.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vk.bot.config.BotCreedsConfig;
import vk.bot.config.ClientConfig;
import vk.bot.error.BotServiceException;
import vk.bot.model.client.HistoryDTO;
import vk.bot.model.client.ResponseHistoryWrapperDTO;

import java.util.Optional;

@Service
@Slf4j
public class MessageHistoryServiceBean implements MessageHistoryService {
    private final RestTemplate restTemplate;
    private final String url;

    public MessageHistoryServiceBean(RestTemplate restTemplate, ClientConfig clientConfig, BotCreedsConfig creeds) {
        this.restTemplate = restTemplate;
        String getHistoryUrl = clientConfig.getUrl() + clientConfig.getGetHistoryMethod();
        this.url = UriComponentsBuilder.fromHttpUrl(getHistoryUrl)
                .queryParam("v", clientConfig.getVersionApi())
                .queryParam("access_token", creeds.getAccessToken())
                .queryParam("count", 10)
                .queryParam("offset", 0)
                .queryParam("peer_id", clientConfig.getPeerId())
                .queryParam("group_id", creeds.getGroupId())
                .queryParam("rev", 0)
                .toUriString();
        log.info(url);
    }

    @Override
    public HistoryDTO getHistory() {
        log.debug("getHistory<- url: {}", url);

        ResponseEntity<ResponseHistoryWrapperDTO> response = restTemplate.getForEntity(url, ResponseHistoryWrapperDTO.class);
        log.debug(String.valueOf(response.getBody()));
        return Optional.of(response)
                .filter(r -> {
                    log.debug("status: {}, body: {}", r.getStatusCode(), r.getBody());
                    return r.getStatusCode().is2xxSuccessful();
                })
                .map(ResponseEntity::getBody)
                .map(body -> {
                    ResponseHistoryWrapperDTO responseHistoryWrapper = response.getBody();
                    HistoryDTO history = responseHistoryWrapper.getResponse();
                    log.debug(history.toString());
                    return history;
                })
                .orElseThrow(() -> {
                    log.error("Failed to fetch history");
                    throw new BotServiceException("Failed to fetch history");
                });
    }

}
