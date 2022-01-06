package com.baidu.hugegraph.structure.space;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SchemaTemplate {
    @JsonProperty("name")
    private String name;
    @JsonProperty("schema")
    private String schema;

    public SchemaTemplate() {
    }

    public SchemaTemplate(String name, String schema) {
        this.name = name;
        this.schema = schema;
    }

    public void name(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public String schema() {
        return this.schema;
    }

    public void schema(String schema) {
        this.schema = schema;
    }

    public Map<String, String> asMap() {
        return ImmutableMap.of("name", this.name, "schema", this.schema);
    }

    public static SchemaTemplate fromMap(Map<String , String> map) {
        return new SchemaTemplate(map.get("name"), map.get("schema"));
    }
}
