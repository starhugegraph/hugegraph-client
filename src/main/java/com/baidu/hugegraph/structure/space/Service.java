package com.baidu.hugegraph.structure.space;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class Service {
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;

    @JsonProperty("url")
    private Set<String> urls;
}
