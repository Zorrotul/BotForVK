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
import vk.bot.config.BotCreeds;
import vk.bot.error.SendMessageException;

import java.util.List;
import java.util.Random;

@Slf4j
@Data
@Service
public class BotService {

    private final BotCreeds creeds;
    private final VkApiClient vk;
    private final GroupActor actor;
    private final Random random;

    public BotService(BotCreeds creeds) {
        this.creeds = creeds;
        log.info("AuthService<-");
        log.info("getGroupId: {}, getGroupToken: {}", creeds.getGroupId(), creeds.getGroupToken());
        TransportClient transportClient = new HttpTransportClient();
        this.vk = new VkApiClient(transportClient);
        this.random = new Random();
        this.actor = new GroupActor(creeds.getGroupId(), creeds.getGroupToken());
    }

    public List<Message> readMessage(Integer ts) {
        MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
        List<Message> messages = null;
        try {
            messages = historyQuery.execute().getMessages().getItems();
            log.info("Messages = {}", messages);
            return messages;
        } catch (ApiException | ClientException e) {
            log.error("Cant read message", e);
            throw new SendMessageException(e);
        }

    }

    public void sendMessage(Message message) {
        try {
            vk.messages().send(actor).message(message.getText())
                    .userId(message.getFromId()).randomId(random.nextInt(10000)).execute();

        } catch (ApiException |
                 ClientException e) {
            log.error("", e);
        }
    }


    public Integer getNewTs() {
        Integer ts;
        try {
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
        } catch (ApiException | ClientException e) {
            log.error("Cant get new ts", e);
            throw new SendMessageException(e);
        }
        return ts;
    }
}