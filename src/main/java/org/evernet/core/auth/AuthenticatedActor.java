package org.evernet.core.auth;

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

    private String identifier;

    private String sourceNodeIdentifier;

    private String sourceVertex;

    private String targetNodeIdentifier;

    private String targetVertex;

    public String getAddress() {
        return String.format("%s/%s/%s", sourceVertex, sourceNodeIdentifier, identifier);
    }

    public String getSourceNodeAddress() {
        return String.format("%s/%s", sourceVertex, sourceNodeIdentifier);
    }

    public String getTargetNodeAddress() {
        return String.format("%s/%s", targetVertex, targetNodeIdentifier);
    }

    public Boolean isLocal() {
        return targetVertex.equals(sourceVertex) && targetNodeIdentifier.equals(sourceNodeIdentifier);
    }
}
