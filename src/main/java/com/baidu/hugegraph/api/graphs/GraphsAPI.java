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

package com.baidu.hugegraph.api.graphs;

import java.util.List;
import java.util.Map;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.google.common.collect.ImmutableMap;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.exception.InvalidResponseException;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.constant.GraphMode;
import com.baidu.hugegraph.structure.constant.GraphReadMode;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.util.E;

public class GraphsAPI extends API {

    private static final String DELIMITER = "/";
    private static final String MODE = "mode";
    private static final String GRAPH_READ_MODE = "graph_read_mode";
    private static final String CLEARED = "cleared";
    private static final String RELOADED = "reloaded";
    private static final String GRAPHS = "graphs";
    private static final String MANAGE = "manage";
    private static final String PATH = "graphspaces/%s/graphs";

    public GraphsAPI(RestClient client, String graphSpace) {
        super(client);
        this.path(String.format(PATH, graphSpace));
    }

    @Override
    protected String type() {
        return HugeType.GRAPHS.string();
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> create(String name, String config) {
        this.client.checkApiVersion("0.67", "dynamic graph add");
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
        RestResult result = this.client.post(joinPath(this.path(), name),
                                             config, headers);
        return result.readObject(Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> get(String name) {
        RestResult result = this.client.get(this.path(), name);
        return result.readObject(Map.class);
    }

    public List<String> list() {
        RestResult result = this.client.get(this.path());
        return result.readList(this.type(), String.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> clear(String name, String message) {
        RestResult result = this.client.put(this.path(), name,
                                            ImmutableMap.of("action", "clear"));
        Map<String, String> response = result.readObject(Map.class);

        E.checkState(response.size() == 1 && response.containsKey(name),
                     "Response must be formatted to {\"%s\" : status}, " +
                     "but got %s", name, response);
        String status = response.get(name);
        E.checkState(CLEARED.equals(status),
                     "Graph %s status must be %s, but got '%s'", name, status);
        return response;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> clear(String name, boolean clearSchema) {
        RestResult result = this.client.put(this.path(), name,
                                            ImmutableMap.of("action", "clear",
                                                            "clear_schema",
                                                            clearSchema));
        Map<String, String> response = result.readObject(Map.class);

        E.checkState(response.size() == 1 && response.containsKey(name),
                     "Response must be formatted to {\"%s\" : status}, " +
                             "but got %s", name, response);
        String status = response.get(name);
        E.checkState(CLEARED.equals(status),
                     "Graph %s status must be %s, but got '%s'", name, status);
        return response;
    }

    public void delete(String graph, String message) {
        this.client.checkApiVersion("0.67", "dynamic graph delete");
        this.client.delete(this.path(), graph);
    }

    public Map<String, String> reload(String name) {
        RestResult result = this.client.put(this.path(), name,
                                            ImmutableMap.of("action",
                                                            "reload"));
        Map<String, String> response = result.readObject(Map.class);

        E.checkState(response.size() == 1 && response.containsKey(name),
                     "Response must be formatted to {\"%s\" : status}, " +
                     "but got %s", name, response);
        String status = response.get(name);
        E.checkState(RELOADED.equals(status),
                     "Graph %s status must be %s, but got '%s'", name, status);
        return response;
    }

    public Map<String, String> reload() {
        RestResult result = this.client.put(this.path(), MANAGE,
                                            ImmutableMap.of("action", "reload"));
        Map<String, String> response = result.readObject(Map.class);

        E.checkState(response.size() == 1 && response.containsKey(GRAPHS),
                     "Response must be formatted to {\"%s\" : status}, " +
                     "but got %s", GRAPHS, response);
        String status = response.get(GRAPHS);
        E.checkState(RELOADED.equals(status),
                     "Server status must be %s, but got '%s'", status);
        return response;
    }

    public void mode(String graph, GraphMode mode) {
        // NOTE: Must provide id for PUT. If use "graph/mode", "/" will
        // be encoded to "%2F". So use "mode" here although inaccurate.
        this.client.put(joinPath(this.path(), graph), MODE, mode);
    }

    public GraphMode mode(String graph) {
        RestResult result =  this.client.get(joinPath(this.path(), graph),
                                             MODE);
        @SuppressWarnings("unchecked")
        Map<String, String> mode = result.readObject(Map.class);
        String value = mode.get(MODE);
        if (value == null) {
            throw new InvalidResponseException(
                      "Invalid response, expect 'mode' in response");
        }
        try {
            return GraphMode.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidResponseException(
                      "Invalid GraphMode value '%s'", value);
        }
    }

    public void readMode(String graph, GraphReadMode readMode) {
        this.client.checkApiVersion("0.59", "graph read mode");
        // NOTE: Must provide id for PUT. If use "graph/graph_read_mode", "/"
        // will be encoded to "%2F". So use "graph_read_mode" here although
        // inaccurate.
        this.client.put(joinPath(this.path(), graph),
                        GRAPH_READ_MODE, readMode);
    }

    public GraphReadMode readMode(String graph) {
        this.client.checkApiVersion("0.59", "graph read mode");
        RestResult result =  this.client.get(joinPath(this.path(), graph),
                                             GRAPH_READ_MODE);
        @SuppressWarnings("unchecked")
        Map<String, String> readMode = result.readObject(Map.class);
        String value = readMode.get(GRAPH_READ_MODE);
        if (value == null) {
            throw new InvalidResponseException(
                      "Invalid response, expect 'graph_read_mode' in response");
        }
        try {
            return GraphReadMode.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidResponseException(
                      "Invalid GraphReadMode value '%s'", value);
        }
    }

    private static String joinPath(String path, String id) {
        return String.join(DELIMITER, path, id);
    }
}
