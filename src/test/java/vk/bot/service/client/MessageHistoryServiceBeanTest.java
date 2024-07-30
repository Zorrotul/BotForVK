package vk.bot.service.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import vk.bot.model.client.HistoryDTO;
import vk.bot.service.BotSchedulerStarter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@MockBean(BotSchedulerStarter.class)
@WireMockTest
class MessageHistoryServiceBeanTest {


    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private WireMockServer wiremockServer = new WireMockServer(8089);

    private final String response = "{\"response\":{\"count\":649,\"items\":[{\"date\":1722317723,\"from_id\":161736917,\"id\":653,\"out\":0,\"version\":10001545,\"attachments\":[],\"conversation_message_id\":650,\"fwd_messages\":[],\"important\":false,\"is_hidden\":false,\"peer_id\":161736917,\"random_id\":0,\"text\":\"7\"}]}}";

    @Autowired
    private MessageHistoryServiceBean messageHistoryServiceBean;

    @BeforeEach
    public void setUp() {
        System.out.println("Start test");
        MockitoAnnotations.openMocks(this);

        wiremockServer.start();

        configureFor("localhost", 8089);

        stubFor(get(urlEqualTo("/method/messages.getHistory?v=5.199&access_token=test_token&count=10&offset=0&peer_id=12345&group_id=67890&rev=0"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(response)
                        .withHeader("Content-Type", "application/json")));
    }

    @Test
    public void testGetHistory() {
        HistoryDTO history = messageHistoryServiceBean.getHistory();

        assertEquals(1, history.getMessages().size());
    }

}
