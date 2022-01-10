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

package com.baidu.hugegraph.driver;

import java.io.Closeable;

import javax.ws.rs.ProcessingException;

import org.apache.commons.lang3.StringUtils;

import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.ClientException;
import com.baidu.hugegraph.util.VersionUtil;
import com.baidu.hugegraph.version.ClientVersion;

public class HugeClient implements Closeable {

    static {
        ClientVersion.check();
    }
    private String graphSpaceName;
    private String graphName;
    private final RestClient client;
    private VersionManager version;
    private GraphsManager graphs;
    private SchemaManager schema;
    private GraphManager graph;
    private GremlinManager gremlin;
    private TraverserManager traverser;
    private VariablesManager variable;
    private JobManager job;
    private TaskManager task;
    private ComputerManager computer;
    private AuthManager auth;
    private MetricsManager metrics;
    private GraphSpaceManager graphSpace;
    private ServiceManager serviceManager;
    private SchemaTemplateManager schemaTemplageManager;

    public HugeClient(HugeClientBuilder builder) {
        this.graphSpaceName = builder.graphSpace();
        this.graphName = builder.graph();

        try {
            if (StringUtils.isEmpty(builder.token())) {
                this.client = new RestClient(builder.url(),
                                             builder.username(),
                                             builder.password(),
                                             builder.timeout(),
                                             builder.maxConns(),
                                             builder.maxConnsPerRoute(),
                                             builder.trustStoreFile(),
                                             builder.trustStorePassword());
            } else {
                this.client = new RestClient(builder.url(),
                                             builder.token(),
                                             builder.timeout(),
                                             builder.maxConns(),
                                             builder.maxConnsPerRoute(),
                                             builder.trustStoreFile(),
                                             builder.trustStorePassword());
            }
        } catch (ProcessingException e) {
            throw new ClientException("Failed to connect url '%s'",
                                      builder.url());
        }
        try {
            this.initManagers(this.client, builder.graphSpace(),
                              builder.graph());
        } catch (Throwable e) {
            this.client.close();
            throw e;
        }
    }

    public static HugeClientBuilder builder(String url, String graphSpace,
                                            String graph) {
        return new HugeClientBuilder(url, graphSpace, graph);
    }

    public HugeClient assignGraph(String graph) {
        this.initManagers(this.client, graphSpaceName, graphName);
        return this;
    }

    @Override
    public void close() {
        this.client.close();
    }

    private void initManagers(RestClient client, String graphSpace,
                              String graph) {
        assert client != null;
        assert graphSpace != null;
        // Check hugegraph-server api version
        this.version = new VersionManager(client);
        this.checkServerApiVersion();

        this.graphs = new GraphsManager(client, graphSpace);
        this.schema = new SchemaManager(client, graphSpace, graph);
        this.graph = new GraphManager(client, graphSpace, graph);
        this.gremlin = new GremlinManager(client, graphSpace, graph, this.graph);
        this.traverser = new TraverserManager(client, this.graph);
        this.variable = new VariablesManager(client, graphSpace, graph);
        this.job = new JobManager(client, graphSpace, graph);
        this.task = new TaskManager(client, graphSpace, graph);
        this.computer = new ComputerManager(client, graphSpace, graph);
        this.auth = new AuthManager(client, graphSpace);
        this.metrics = new MetricsManager(client);
        this.graphSpace = new GraphSpaceManager(client);
        this.schemaTemplageManager = new SchemaTemplateManager(client, graphSpace);
        this.serviceManager = new ServiceManager(client, graphSpace);
    }

    private void checkServerApiVersion() {
        VersionUtil.Version apiVersion = VersionUtil.Version.of(
                                         this.version.getApiVersion());
        VersionUtil.check(apiVersion, "3.0", "3.1",
                          "hugegraph-api in server");
        this.client.apiVersion(apiVersion);
    }

    public String getGraphSpaceName() {
        return graphSpaceName;
    }

    public String getGraphName() {
        return graphName;
    }

    public GraphsManager graphs() {
        return this.graphs;
    }

    public SchemaManager schema() {
        return this.schema;
    }

    public GraphManager graph() {
        return this.graph;
    }

    public GremlinManager gremlin() {
        return this.gremlin;
    }

    public TraverserManager traverser() {
        return this.traverser;
    }

    public VariablesManager variables() {
        return this.variable;
    }

    public JobManager job() {
        return this.job;
    }

    public ComputerManager computer() {
        return this.computer;
    }

    public TaskManager task() {
        return this.task;
    }

    public AuthManager auth() {
        return this.auth;
    }

    public MetricsManager metrics() {
        return this.metrics;
    }

    public GraphSpaceManager graphSpace() {
        return this.graphSpace;
    }

    public SchemaTemplateManager schemaTemplateManager() {
        return this.schemaTemplageManager;
    }

    public ServiceManager serviceManager() {
        return this.serviceManager;
    }

    public void setAuthContext(String auth) {
        this.client.setAuthContext(auth);
    }

    public String getAuthContext() {
        return this.client.getAuthContext();
    }

    public void resetAuthContext() {
        this.client.resetAuthContext();
    }
}
