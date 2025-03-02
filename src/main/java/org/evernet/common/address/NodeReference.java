package org.evernet.common.address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evernet.common.exception.ClientException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class NodeReference {

    private String identifier;

    private String vertex;

    public String getAddress() {
        return String.format("%s/%s", vertex, identifier);
    }

    public static NodeReference from(String str) {
        String[] parts = str.split("/");

        if (parts.length != 2) {
            throw new ClientException(String.format("Invalid node reference %s", str));
        }

        return NodeReference.builder().vertex(parts[0]).identifier(parts[1]).build();
    }
}
