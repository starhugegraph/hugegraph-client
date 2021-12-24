package com.baidu.hugegraph.structure.space;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OLTPService {
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("deployment_type")
    private DepleymentType depleymentType;

    @JsonProperty("running")
    private int running; // 当前运行节点数
    @JsonProperty("count")
    private int count; // 最大可运行节点

    @JsonProperty("cpu_limit")
    private int cpuLimit;
    @JsonProperty("memory_limit")
    private int memoryLimit; // GB

    @JsonProperty("urls")
    private Set<String> urls;

    enum DepleymentType {
        K8S,
        MENUAL;
    }
}
