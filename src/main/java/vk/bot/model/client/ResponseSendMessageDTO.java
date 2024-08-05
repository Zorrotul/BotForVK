package vk.bot.model.client;

import jakarta.validation.constraints.AssertTrue;
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

    @AssertTrue(message = "Unknown response")
    public boolean isResponseValid() {
        return response != null || error != null;
    }
}
