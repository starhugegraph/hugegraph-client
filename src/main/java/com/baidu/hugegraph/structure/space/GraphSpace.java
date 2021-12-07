package com.baidu.hugegraph.structure.space;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GraphSpace {
    @JsonProperty("name")
    public String name;
    @JsonProperty("max_graph_number")
    public int maxGraphNumber;
    @JsonProperty("max_role_number")
    public int maxRoleNumber;
    @JsonProperty("configs")
    public Map<String, Object> configs;

    public GraphSpace(String name) {
        this.name = name;
        this.maxGraphNumber = Integer.MAX_VALUE;
        this.maxRoleNumber = Integer.MAX_VALUE;
        this.configs = new ConcurrentHashMap<>();
    }

    public interface Builder {
        GraphSpace build();
        Builder maxGraphNumnber(int number);
        Builder maxRoleNumber(int maxRoleNumber);
        Builder configs(Map<String, Object> configs);
    }

    public static class BuilderImpl implements Builder {

        private GraphSpace graphSpace;

        public BuilderImpl(String name) {
            this.graphSpace = new GraphSpace(name);
        }

        @Override
        public GraphSpace build() {
            return this.graphSpace;
        }

        @Override
        public Builder maxGraphNumnber(int maxGraphNumber) {
            this.graphSpace.maxGraphNumber = maxGraphNumber;;
            return this;
        }

        @Override
        public Builder maxRoleNumber(int maxRoleNumber) {
            this.graphSpace.maxRoleNumber = maxRoleNumber;
            return this;
        }

        @Override
        public Builder configs(Map<String, Object> configs) {
            this.graphSpace.configs = configs;
            return this;
        }

    }

}
