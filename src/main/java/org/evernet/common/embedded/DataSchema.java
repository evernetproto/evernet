package org.evernet.common.embedded;

import java.io.Serializable;
import java.util.Map;

public class DataSchema implements Serializable {

    private Map<String, FieldSchema> fieldSchemas;

    public DataSchema() {

    }

    public Map<String, FieldSchema> getFieldSchemas() {
        return fieldSchemas;
    }

    public void setFieldSchemas(Map<String, FieldSchema> fieldSchemas) {
        this.fieldSchemas = fieldSchemas;
    }
}
