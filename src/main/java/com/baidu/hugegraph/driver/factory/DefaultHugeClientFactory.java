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

package com.baidu.hugegraph.driver.factory;

import java.util.concurrent.ConcurrentHashMap;

import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.util.E;
import com.google.common.base.Strings;

public class DefaultHugeClientFactory {

    private final String defaultHugeGraph = "hugegraph";
    private final String[] urls;

    private static final ConcurrentHashMap<String, HugeClient> CLIENT_CACHES
            = new ConcurrentHashMap();
    private static final String CLIENT_KEY_PATTERN = "%s-%s-%s-%s-%s";

    public DefaultHugeClientFactory(String[] urls) {
        this.urls = urls;
    }

    public HugeClient createClient(String graphSpace, String graph) {
        return this.createClient(graphSpace, graph, 60);
    }

    public HugeClient createClient(String graphSpace, String graph,
                                   int timeout) {
        return this.createClient(graphSpace, graph, null, null, null, timeout);
    }

    public HugeClient createClient(String graphSpace, String graph,
                                   String token, String username,
                                   String password) {
        return createClient(graphSpace, graph, token, username, password, 60);
    }

    public HugeClient createClient(String graphSpace, String graph,
                                   String token, String username,
                                   String password, int timeout) {
        E.checkArgument(timeout > 0, "Client timeout must > 0");

        int r = (int) Math.floor(Math.random() * urls.length);
        String url = this.urls[r];

        graph = graph == null ? defaultHugeGraph : graph;

        String key = Strings.lenientFormat(CLIENT_KEY_PATTERN, url, token,
                                           username, password, timeout);



        HugeClient client = CLIENT_CACHES.get(key);

        if (client == null) {
            synchronized (CLIENT_CACHES) {
                client = CLIENT_CACHES.get(key);
                if (client == null) {
                    client = HugeClient.builder(url, graphSpace, graph)
                                       .configToken(token)
                                       .configUser(username, password)
                                       .configTimeout(timeout)
                                       .build();

                    CLIENT_CACHES.put(key, client);
                }
            }
        }

        return client;
    }
}
