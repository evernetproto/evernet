package org.evernet.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class StorageSchemaAddress {

    private String vertexEndpoint;

    private String nodeIdentifier;

    private String identifier;

    public static StorageSchemaAddress fromString(String address) {
        String[] components = address.split("/");

        if (components.length != 5) {
            throw new IllegalArgumentException(String.format("Invalid storage schema address %s", address));
        }

        if (!components[2].equals("storage") || !components[3].equals("schema")) {
            throw new IllegalArgumentException(String.format("Invalid storage schema address %s", address));
        }

        return StorageSchemaAddress.builder()
                .vertexEndpoint(components[0])
                .nodeIdentifier(components[1])
                .identifier(components[4])
                .build();
    }

    public String toString() {
        return String.format("%s/%s/storage/schema/%s", vertexEndpoint, nodeIdentifier, identifier);
    }
}
