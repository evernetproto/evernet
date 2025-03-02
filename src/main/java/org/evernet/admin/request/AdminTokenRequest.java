package org.evernet.admin.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class AdminTokenRequest {

    @NotBlank(message = "Identifier is required")
    @Size(min = 3, max = 32, message = "Identifier must be between 3 to 32 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Identifier must be alphanumeric")
    private String identifier;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 32, message = "Password must be between 6 to 32 characters long")
    private String password;
}
