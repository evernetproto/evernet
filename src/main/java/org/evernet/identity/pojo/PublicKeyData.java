package org.evernet.identity.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeyData implements Serializable {

    private String string;

    @BsonIgnore
    @JsonIgnore
    @Transient
    private PublicKey object;

    private String node;

    private String identifier;

    public PublicKeyData() {

    }

    public PublicKeyData(String string, String node, String identifier) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.string = string;
        this.object = convertToPublicKeyFromString(string);
        this.node = node;
        this.identifier = identifier;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public PublicKey getObject() {
        return object;
    }

    public void setObject(PublicKey object) {
        this.object = object;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public static PublicKey convertToPublicKeyFromString(String publicKeyString) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] publicKeyBytes = Base64.decodeBase64(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
