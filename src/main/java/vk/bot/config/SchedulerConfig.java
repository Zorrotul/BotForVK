package vk.bot.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.config.scheduler")
@Valid
public class SchedulerConfig {

    @NotNull
    private Long period;

    @NotNull
    private Long awaitTerminationMillis;

    @NotBlank
    private String threadNamePrefix;

}
