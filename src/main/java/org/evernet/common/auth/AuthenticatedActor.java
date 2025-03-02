package org.evernet.common.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.evernet.common.address.ActorReference;
import org.evernet.common.address.NodeReference;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
public class AuthenticatedActor {

    private ActorReference actor;

    private NodeReference targetNode;

    public Boolean isLocal() {
        return actor.getNode().getAddress().equals(targetNode.getAddress());
    }
}
