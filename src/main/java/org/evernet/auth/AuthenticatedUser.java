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
public class AuthenticatedUser {

    private String userIdentifier;

    private String userNodeIdentifier;

    private String userVertexEndpoint;

    private String userNodeAddress;

    private String userAddress;

    private String targetNodeIdentifier;

    private String targetNodeAddress;

    private String targetVertexEndpoint;
}