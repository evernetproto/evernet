package org.evernet.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evernet.exception.ClientException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class UserAddress {

    private String userIdentifier;

    private NodeAddress nodeAddress;

    public static UserAddress fromString(String address) {
        String[] components = address.split("/");

        if (components.length != 3) {
            throw new ClientException(String.format("Invalid user address %s", address));
        }

        return UserAddress.builder()
                .nodeAddress(NodeAddress.builder().vertexEndpoint(components[0]).nodeIdentifier(components[1]).build())
                .userIdentifier(components[2])
                .build();
    }

    public String toString() {
        return String.format("%s/%s", nodeAddress.toString(), userIdentifier);
    }
}