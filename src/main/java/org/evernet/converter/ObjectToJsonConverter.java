package org.evernet.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.evernet.util.Json;

@Converter
public class ObjectToJsonConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return Json.encode(attribute);
    }

    @Override
    public Object convertToEntityAttribute(String s) {
        return Json.decode(s, Object.class);
    }
}