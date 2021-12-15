package com.baidu.hugegraph.driver.factory;

import com.baidu.hugegraph.driver.HugeClient;

public class DefaultHugeClientFactory {

    private String[] urls;

    public DefaultHugeClientFactory(String[] urls) {
        this.urls = urls;
    }

    public HugeClient createClient(String graphSpace, String graph) {
        return this.createClient(graphSpace, graph, null);
    }

    public HugeClient createClient(String graphSpace, String graph,
                                 String token) {
        int r = (int) Math.floor(Math.random() * urls.length);
        HugeClient client =
                HugeClient.builder(this.urls[r], graphSpace, graph)
                          .configToken(token).build();
        return client;
    }
}
