package org.evernet.util;

import lombok.experimental.UtilityClass;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.util.Base64;

@UtilityClass
public class Ed25519KeyHelper {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519", "BC");
        return keyPairGenerator.generateKeyPair();
    }

    public String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public PublicKey stringToPublicKey(String base64PublicKey) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519", "BC");
        return keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(keyBytes));
    }

    public PrivateKey stringToPrivateKey(String base64PrivateKey) throws GeneralSecurityException {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("Ed25519", "BC");
        return keyFactory.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(keyBytes));
    }
}