package vk.bot.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryDTO implements Serializable {

    @NotNull
    private Long count;

    @NotNull
    @JsonProperty("items")
    private List<VkMessageDTO> messages;

}
