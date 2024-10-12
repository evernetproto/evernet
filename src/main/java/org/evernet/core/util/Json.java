package org.evernet.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Object decode(String s, Class<?> c) {
        Object u = null;

        if (s != null) {
            try {
                u = objectMapper.readerFor(c).readValue(s);
            } catch (Exception e) {
                // Do nothing
            }
        }

        return u;
    }

    public static String encode(Object o) {
        if (o == null) {
            return "";
        }

        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            // Do nothing
        }

        return null;
    }
}