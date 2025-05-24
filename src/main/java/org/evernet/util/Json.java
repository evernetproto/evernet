package org.evernet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Json {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Object decode(String s, Class<?> c) {
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

    public String encode(Object o) {
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