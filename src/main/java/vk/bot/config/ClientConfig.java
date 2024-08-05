package vk.bot.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.config.client")
@Valid
public class ClientConfig {

    @NotNull
    private Long peerId;

    @NotBlank
    private String url;

    @NotBlank
    private String versionApi;

    @NotBlank
    private String getHistoryMethod;

    @NotBlank
    private String sendMessageMethod;

    @NotNull
    private AtomicLong randomId = new AtomicLong(LocalTime.now().getNano());

    @NotNull
    private Integer connectTimeout;

    @NotNull
    private Integer readTimeout;


    public Long getRandomId() {
        return randomId.incrementAndGet();
    }
}

