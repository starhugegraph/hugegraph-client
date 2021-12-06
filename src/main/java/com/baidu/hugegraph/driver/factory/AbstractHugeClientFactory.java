package com.baidu.hugegraph.driver.factory;

import com.baidu.hugegraph.driver.HugeClient;

public abstract class AbstractHugeClientFactory {

    public HugeClient getClient(String graphSpace, String graph) {
        return getClient(graphSpace, graph, null);
    }

    public HugeClient getClient(String graphSpace, String graph, String token) {
        return createClient(graphSpace, graph, token);
    }

    abstract HugeClient createClient(String graphSpace, String graph,
                                     String token);
}
