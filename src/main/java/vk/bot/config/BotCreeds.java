package vk.bot.config;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
//@PropertySource("classpath:creeds.yml")
@ConfigurationProperties(prefix = "app.config.creeds")
@Valid
public class BotCreeds {
    @NotNull
    private Integer groupId;

    @NotBlank
    private String groupToken;

}


