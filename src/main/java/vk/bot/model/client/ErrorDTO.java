package vk.bot.model.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asynchttpclient.Request;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDTO {

    @NotNull
    @JsonProperty("error_code")
    private Integer errorCode;

    @NotBlank
    @JsonProperty("error_msg")
    private String errorMsg;

    @NotNull
    @JsonProperty("request_params")
    private List<ErrorRequestParamsDTO> requestParams;

}
