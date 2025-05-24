package org.evernet.auth;

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
public class AuthenticatedNode {

    private String nodeIdentifier;

    private String nodeVertexEndpoint;

    private String nodeAddress;

    private String targetNodeIdentifier;

    private String targetNodeAddress;

    private String targetVertexEndpoint;
}
