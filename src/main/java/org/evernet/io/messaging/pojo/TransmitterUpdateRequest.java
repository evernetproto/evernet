package org.evernet.io.messaging.pojo;

import java.io.Serializable;

public class TransmitterUpdateRequest implements Serializable {

    private String displayName;

    private String description;

    public TransmitterUpdateRequest() {

    }

    public TransmitterUpdateRequest(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
