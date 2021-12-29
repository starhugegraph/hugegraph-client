package com.baidu.hugegraph.driver.factory;

import com.baidu.hugegraph.driver.HugeClient;

public class DefaultHugeClientFactory {

    private final String defaultHugeGraph = "hugegraph";
    private final String[] urls;

    public DefaultHugeClientFactory(String[] urls) {
        this.urls = urls;
    }

    public HugeClient createClient(String graphSpace, String graph) {
        return this.createClient(graphSpace, graph, null, null, null);
    }

    public HugeClient createClient(String graphSpace, String graph,
                                   String token, String username,
                                   String password) {
        int r = (int) Math.floor(Math.random() * urls.length);

        graph = graph == null ? defaultHugeGraph : graph;

        HugeClient client =
                HugeClient.builder(this.urls[r], graphSpace, graph)
                          .configToken(token).configUser(username, password)
                          .build();
        return client;
    }
}
