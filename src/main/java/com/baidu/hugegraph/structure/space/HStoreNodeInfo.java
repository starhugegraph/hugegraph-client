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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HStoreNodeInfo {
    // Node id
    @JsonProperty("id")
    private String id;
    // 总空间大小
    @JsonProperty("capacity")
    private long capacity;
    // 已使用空间
    @JsonProperty("used")
    private long used;

    // 节点状态
    @JsonProperty("state")
    private String state;

    // partitions
    @JsonProperty("partitions")
    List<HStorePartitionInfo> hStorePartitionInfoList;

    public static class HStorePartitionInfo {
        @JsonProperty("id")
        private int id;
        @JsonProperty("graph_name")
        private String graphName;

        public HStorePartitionInfo() {
        }

        public HStorePartitionInfo(int id, String graphName) {
            this.id = id;
            this.graphName = graphName;
        }

        public int id() {
            return id;
        }

        public void id(int id) {
            this.id = id;
        }

        public String graphName() {
            return graphName;
        }

        public void graphName(String graphName) {
            this.graphName = graphName;
        }
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
    }

    public long capacity() {
        return capacity;
    }

    public void capacity(long capacity) {
        this.capacity = capacity;
    }

    public long used() {
        return used;
    }

    public void used(long used) {
        this.used = used;
    }

    public List<HStorePartitionInfo> hStorePartitionInfoList() {
        return hStorePartitionInfoList;
    }

    public void hStorePartitionInfoList(
            List<HStorePartitionInfo> hStorePartitionInfoList) {
        this.hStorePartitionInfoList = hStorePartitionInfoList;
    }
}
