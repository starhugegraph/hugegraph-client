package com.baidu.hugegraph.structure.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserManager {

    @JsonProperty("user")
    private String user;
    @JsonProperty("type")
    private HugePermission type;
    @JsonProperty("graphspace")
    private String graphSpace;
}
