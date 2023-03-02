package org.evernet.identity.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Document(collection = "known_entities")
public class KnownEntity implements Serializable {

    @Id
    private String id;

    private String publicKey;

    private String defaultIdentifier;

    private Set<String> identifiers;

    private String defaultNode;

    private Set<String> nodes;

    private String defaultType;

    private Set<String> types;

    private String defaultDisplayName;

    private Set<String> displayNames;

    private String description;

    private Date createdOn;

    private Date modifiedOn;

    public KnownEntity() {

    }

    public KnownEntity(String publicKey, String defaultIdentifier, Set<String> identifiers, String defaultNode, Set<String> nodes, String defaultType, Set<String> types, String defaultDisplayName, Set<String> displayNames, String description) {
        this.publicKey = publicKey;
        this.defaultIdentifier = defaultIdentifier;
        this.identifiers = identifiers;
        this.defaultNode = defaultNode;
        this.nodes = nodes;
        this.defaultType = defaultType;
        this.types = types;
        this.defaultDisplayName = defaultDisplayName;
        this.displayNames = displayNames;
        this.description = description;
        this.createdOn = new Date();
        this.modifiedOn = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getDefaultIdentifier() {
        return defaultIdentifier;
    }

    public void setDefaultIdentifier(String defaultIdentifier) {
        this.defaultIdentifier = defaultIdentifier;
    }

    public Set<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Set<String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getDefaultNode() {
        return defaultNode;
    }

    public void setDefaultNode(String defaultNode) {
        this.defaultNode = defaultNode;
    }

    public Set<String> getNodes() {
        return nodes;
    }

    public void setNodes(Set<String> nodes) {
        this.nodes = nodes;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }

    public void setDefaultDisplayName(String defaultDisplayName) {
        this.defaultDisplayName = defaultDisplayName;
    }

    public Set<String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(Set<String> displayNames) {
        this.displayNames = displayNames;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
