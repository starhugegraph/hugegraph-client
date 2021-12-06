package com.baidu.hugegraph.driver.factory;

import com.baidu.hugegraph.driver.HugeClient;

public class DefaultHugeClientFactory extends AbstractHugeClientFactory {

    private String url;

    public DefaultHugeClientFactory(String url) {
        this.url = url;
    }

    @Override
    public HugeClient getClient(String spacename, String graph, String token) {
        return this.createClient(spacename, graph, token);
    }

    @Override
    HugeClient createClient(String graphSpace, String graph, String token) {
        HugeClient client =
                HugeClient.builder(this.url, graphSpace, graph).build();
        return client;
    }
}
