/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.structure.space;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphSpaceReq {

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
    @JsonProperty("max_graph_number")
    private int maxGraphNumber = Integer.MAX_VALUE;
    @JsonProperty("max_role_number")
    private int maxRoleNumber = Integer.MAX_VALUE;

    @JsonProperty("oltp_namespace")
    public String oltpNamespace;
    @JsonProperty("olap_namespace")
    private String olapNamespace;
    @JsonProperty("storage_namespace")
    private String storageNamespace;

    @JsonProperty("configs")
    private Map<String, Object> configs = new HashMap<>();

    public GraphSpaceReq() {
    }

    public GraphSpaceReq(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public GraphSpaceReq setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public GraphSpaceReq setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getCpuLimit() {
        return cpuLimit;
    }

    public GraphSpaceReq setCpuLimit(int cpuLimit) {
        this.cpuLimit = cpuLimit;
        return this;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public GraphSpaceReq setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
        return this;
    }

    public int getStorageLimit() {
        return storageLimit;
    }

    public GraphSpaceReq setStorageLimit(int storageLimit) {
        this.storageLimit = storageLimit;
        return this;
    }

    public String getOltpNamespace() {
        return oltpNamespace;
    }

    public GraphSpaceReq setOltpNamespace(String oltpNamespace) {
        this.oltpNamespace = oltpNamespace;
        return this;
    }

    public String getOlapNamespace() {
        return olapNamespace;
    }

    public GraphSpaceReq setOlapNamespace(String olapNamespace) {
        this.olapNamespace = olapNamespace;
        return this;
    }

    public String getStorageNamespace() {
        return storageNamespace;
    }

    public GraphSpaceReq setStorageNamespace(String storageNamespace) {
        this.storageNamespace = storageNamespace;
        return this;
    }

    public int getMaxGraphNumber() {
        return maxGraphNumber;
    }

    public GraphSpaceReq setMaxGraphNumber(int maxGraphNumber) {
        this.maxGraphNumber = maxGraphNumber;
        return this;
    }

    public int getMaxRoleNumber() {
        return maxRoleNumber;
    }

    public GraphSpaceReq setMaxRoleNumber(int maxRoleNumber) {
        this.maxRoleNumber = maxRoleNumber;
        return this;
    }

    public Map<String, Object> getConfigs() {
        return configs;
    }

    public GraphSpaceReq setConfigs(
            Map<String, Object> configs) {
        this.configs = configs == null ? new HashMap<>() : configs;
        return this;
    }
}
