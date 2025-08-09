package org.evernet.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evernet.model.MessageTypeHandler;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class MessageTypeHandlerRequest {

    @NotBlank(message = "Message type address is required")
    private String messageTypeAddress;

    @NotNull(message = "Message type handler location is required")
    private MessageTypeHandler.Location location;

    @NotNull(message = "Handler type is required")
    private MessageTypeHandler.HandlerType handlerType;

    @NotBlank(message = "Handler address is required")
    private String handlerAddress;
}
