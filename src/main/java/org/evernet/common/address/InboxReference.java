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
public class InboxReference {

    private String identifier;

    private String nodeIdentifier;

    private String vertex;

    public String getAddress() {
        return String.format("%s/%s/%s/%s", vertex, nodeIdentifier, "inbox", identifier);
    }

    public static InboxReference from(String str) {
        String[] components = str.split("/");

        if (components.length != 4) {
            throw new ClientException(String.format("Invalid inbox reference %s", str));
        }

        if (!components[2].equals("inbox")) {
            throw new ClientException(String.format("Invalid inbox reference %s", str));
        }

        return InboxReference.builder()
                .vertex(components[0])
                .nodeIdentifier(components[1])
                .identifier(components[3])
                .build();
    }

    public NodeReference getNode() {
        return NodeReference.builder().vertex(vertex).identifier(nodeIdentifier).build();
    }
}
