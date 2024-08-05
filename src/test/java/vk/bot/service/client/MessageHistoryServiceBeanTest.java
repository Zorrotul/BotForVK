package vk.bot.service.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import vk.bot.config.BotCreedsConfig;
import vk.bot.config.ClientConfig;
import vk.bot.error.HandlerException;
import vk.bot.model.client.HistoryDTO;
import vk.bot.service.BotHandler;
import vk.bot.service.BotSchedulerStarter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("DataFlowIssue")
@SpringBootTest
@MockBean(BotSchedulerStarter.class)
@WireMockTest
class MessageHistoryServiceBeanTest {


    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private static final WireMockServer wiremockServer = new WireMockServer(8089);

    private final ClassLoader classLoader = getClass().getClassLoader();

    private String response;

    private String url;

    @Autowired
    MessageHistoryServiceBean messageHistoryService;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    BotHandler handler;

    @BeforeAll
    public static void init() {
        System.out.println("Start tests");
        wiremockServer.start();
        configureFor("localhost", 8089);
    }

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        response = getResponseFromFile("successGetHistory.json");

        url = (String) ReflectionTestUtils.getField(messageHistoryService, "url");
        url = url.substring(clientConfig.getUrl().length() - 1);
    }

    @Test
    public void successGetHistoryMessageTextTest() {

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(response)
                        .withFixedDelay(0)
                        .withHeader("Content-Type", "application/json")));

        HistoryDTO history = (HistoryDTO) ReflectionTestUtils.invokeGetterMethod(handler, "getHistory");

        System.out.println(response);

        assertEquals("7", history.getMessages().get(0).getText());
    }

    @Test
    public void successGetHistoryMessagesSizeTest() {

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(response)
                        .withFixedDelay(0)
                        .withHeader("Content-Type", "application/json")));

        HistoryDTO history = (HistoryDTO) ReflectionTestUtils.invokeGetterMethod(handler, "getHistory");

        System.out.println(response);

        assertEquals(1, history.getMessages().size());
    }

    @Test
    public void negativeGetHistoryTest() {

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody(response)
                        .withFixedDelay(0)
                        .withHeader("Content-Type", "application/json")));

        HandlerException exception = assertThrows(HandlerException.class, () -> ReflectionTestUtils.invokeGetterMethod(handler, "getHistory"));
        assertEquals("Cant get history", exception.getMessage());
    }

    @Test
    public void negativeGetHistoryBadRequestTest() {

        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody(response)
                        .withFixedDelay(0)
                        .withHeader("Content-Type", "application/json")));

        HandlerException exception = assertThrows(HandlerException.class, () -> ReflectionTestUtils.invokeGetterMethod(handler, "getHistory"));
        assertEquals("400 BAD_REQUEST", exception.getMessage());
    }

    private String getResponseFromFile(String name) throws IOException {

        File file = new File(classLoader.getResource(name).getPath());
        return Files.readString(file.toPath());
    }

}
