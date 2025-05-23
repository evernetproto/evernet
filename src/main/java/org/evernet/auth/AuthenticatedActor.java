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
public class AuthenticatedActor {

    private String actorIdentifier;

    private String actorNodeIdentifier;

    private String actorVertexEndpoint;

    private String actorNodeAddress;

    private String actorAddress;

    private String targetNodeIdentifier;

    private String targetNodeAddress;

    private String targetVertexEndpoint;
}
