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
public class ActorReference {

    private String identifier;

    private String nodeIdentifier;

    private String vertex;

    public String getAddress() {
        return String.format("%s/%s/%s/%s", vertex, nodeIdentifier, "actor", identifier);
    }

    public static ActorReference from(String str) {
        String[] components = str.split("/");

        if (components.length != 4) {
            throw new ClientException(String.format("Invalid actor reference %s", str));
        }

        if (!components[2].equals("actor")) {
            throw new ClientException(String.format("Invalid actor reference %s", str));
        }

        return ActorReference.builder()
                .vertex(components[0])
                .nodeIdentifier(components[1])
                .identifier(components[3])
                .build();
    }

    public NodeReference getNode() {
        return NodeReference.builder().vertex(this.vertex).identifier(this.nodeIdentifier).build();
    }
}
