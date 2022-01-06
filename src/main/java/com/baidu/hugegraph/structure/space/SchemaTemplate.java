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
