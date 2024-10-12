package org.evernet.core.util;

import org.mindrot.jbcrypt.BCrypt;

public class Password {

    public static String hash(String password) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }

    public static boolean verify(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}