package org.evernet.auth;

public class AuthenticatedNodeController {

    public AuthenticatedNode getNode() {
        return ThreadLocalWrapper.getNode();
    }

    public String getNodeIdentifier() {
        return getNode().getNodeIdentifier();
    }

    public String getVertexEndpoint() {
        return getNode().getNodeVertexEndpoint();
    }
}