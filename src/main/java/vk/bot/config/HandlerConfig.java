package vk.bot.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.config.handler")
@Valid
public class HandlerConfig {

    @NotNull
    private Integer numberOfAttempts;

    @NotNull
    private Long waitingTimeout;

}
