package vk.bot.service.client;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class MessageHistoryServiceBean {
    private final RestTemplate restTemplate;
    private final ClientConfig clientConfig;
    private final BotCreedsConfig creeds;
    private final String getHistoryUrl = "https://api.vk.com/method/messages.getHistory";


    public HistoryDTO getHistory() {
        log.info("getHistory<-");

        String url = UriComponentsBuilder.fromHttpUrl(getHistoryUrl)
                .queryParam("v", "5.199")
                .queryParam("access_token", creeds.getGroupToken())
                .queryParam("count", 200)
                .queryParam("offset", 0)
                .queryParam("peer_id", clientConfig.getPeerId())
                .queryParam("group_id", clientConfig.getGroupId())
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

}