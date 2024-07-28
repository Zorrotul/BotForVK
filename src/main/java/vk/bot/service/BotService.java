package vk.bot.service;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vk.bot.config.BotCreedsConfig;
import vk.bot.error.BotServiceException;

import java.util.List;
import java.util.Random;

@Slf4j
@Data
@Service
public class BotService {

    private final VkApiClient vk;
    private final GroupActor actor;
    private final Random random;

    public BotService(BotCreedsConfig creeds) {
        log.info("AuthService<- getGroupId: {}, getGroupToken: {}", creeds.getGroupId(), creeds.getGroupToken());
        TransportClient transportClient = new HttpTransportClient();
        this.vk = new VkApiClient(transportClient);
        this.random = new Random();
        this.actor = new GroupActor(creeds.getGroupId(), creeds.getGroupToken());
    }

    public List<Message> readMessage(Integer ts) {
        MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
        log.debug("reading message with ts = {}", ts);
        List<Message> messages;
        try {
            messages = historyQuery.execute().getMessages().getItems();
            log.debug("Messages = {}", messages);
            return messages;
        } catch (ApiException | ClientException e) {
            log.error("Cant read message", e);
            throw new BotServiceException(e);
        }

    }

    public void sendMessage(Message message) {
        try {
            log.info("sending message = {}", message.toString());
            vk.messages().send(actor).message(message.getText())
                    .userId(message.getFromId()).randomId(random.nextInt(10000)).execute();

        } catch (ApiException |
                 ClientException e) {
            log.error("Cant send message", e);
            throw new BotServiceException(e);
        }
    }

    public Integer getNewTs() {
        Integer ts;
        try {
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            log.debug("getNewTs<- ts = {}", ts);
        } catch (ApiException | ClientException e) {
            log.error("Cant get new ts", e);
            throw new BotServiceException(e);
        }
        return ts;
    }
}