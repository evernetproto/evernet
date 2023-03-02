package org.evernet.identity.pojo;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class EntityRegistrationRequest implements Serializable {

    @NotBlank
    private String identifier;

    @NotBlank
    private String displayName;

    @NotBlank
    private String type;

    private String description;

    private String encryptedPrivateKey;

    @NotBlank
    private String publicKey;

    public EntityRegistrationRequest() {

    }

    public EntityRegistrationRequest(String identifier, String displayName, String type, String description, String encryptedPrivateKey, String publicKey) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.type = type;
        this.description = description;
        this.encryptedPrivateKey = encryptedPrivateKey;
        this.publicKey = publicKey;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
