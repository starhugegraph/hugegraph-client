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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties
public class GraphSpace extends GraphSpaceReq{

    public GraphSpace() {
    }
    public GraphSpace(String name) {
        super(name);
    }

    @JsonProperty("cpu_used")
    private int cpuUsed;
    @JsonProperty("memory_used")
    private int memoryUsed; // GB
    @JsonProperty("storage_used")
    private int storageUsed; // GB
    @JsonProperty("graph_number_used")
    private int graphNumberUsed;
    @JsonProperty("role_number_used")
    private int roleNumberUsed;

    public int getCpuUsed() {
        return cpuUsed;
    }

    public GraphSpace setCpuUsed(int cpuUsed) {
        this.cpuUsed = cpuUsed;
        return this;
    }

    public int getMemoryUsed() {
        return memoryUsed;
    }

    public GraphSpace setMemoryUsed(int memoryUsed) {
        this.memoryUsed = memoryUsed;
        return this;
    }

    public int getStorageUsed() {
        return storageUsed;
    }

    public GraphSpace setStorageUsed(int storageUsed) {
        this.storageUsed = storageUsed;
        return this;
    }

    public int getGraphNumberUsed() {
        return graphNumberUsed;
    }

    public GraphSpace setGraphNumberUsed(int graphNumberUsed) {
        this.graphNumberUsed = graphNumberUsed;
        return this;
    }

    public int getRoleNumberUsed() {
        return roleNumberUsed;
    }

    public GraphSpace setRoleNumberUsed(int roleNumberUsed) {
        this.roleNumberUsed = roleNumberUsed;
        return this;
    }
}
