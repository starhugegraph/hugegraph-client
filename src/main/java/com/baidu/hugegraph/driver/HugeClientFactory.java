package com.baidu.hugegraph.driver;

public abstract class HugeClientFactory {

    HugeClient getClient(String graph) {
        return this.getClient("default", graph);
    }

    abstract HugeClient getClient(String spacename, String graph);
}
