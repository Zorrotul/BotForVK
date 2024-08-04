package vk.bot.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseSendMessageDTO {

    private SendMessageDTO response;

    private ErrorDTO error;

}
