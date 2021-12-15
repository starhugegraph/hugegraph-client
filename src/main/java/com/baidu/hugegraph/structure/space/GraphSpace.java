package com.baidu.hugegraph.structure.space;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GraphSpace {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")

    private String description;
    @JsonProperty("cpu_limit")
    private int cpuLimit;
    @JsonProperty("memory_limit")
    private int memoryLimit; // GB
    @JsonProperty("storage_limit")
    public int storageLimit; // GB

    @JsonProperty("oltp_namepsace")
    public String oltpNamespace;
    @JsonProperty("olap_namepsace")
    private String olapNamespace;
    @JsonProperty("storage_namepsace")
    private String storageNamespace;
}
