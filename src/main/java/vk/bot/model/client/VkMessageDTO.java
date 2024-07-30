package vk.bot.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class VkMessageDTO implements Serializable {

    @NotNull
    private Long date;

    @NotNull
    @JsonProperty("from_id")
    private Long fromId;

    @NotNull
    private Long id;

    private Long out;

    private Long version;

    @JsonProperty("conversation_message_id")
    private Long conversationMessageId;

    private boolean important;

    @JsonProperty("is_hidden")
    private boolean isHidden;

    @JsonProperty("peer_id")
    private Long peerId;

    @JsonProperty("random_id")
    private Long randomId;

    private String text;

}
