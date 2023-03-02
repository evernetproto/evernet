package org.evernet.identity.authenticator;

import java.io.Serializable;

public class Token implements Serializable {

    private String audience;

    private String identifier;

    public Token() {

    }

    public Token(String audience, String identifier) {
        this.audience = audience;
        this.identifier = identifier;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
