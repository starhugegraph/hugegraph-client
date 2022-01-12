package com.baidu.hugegraph.api.space;

import java.util.List;
import com.google.common.collect.ImmutableMap;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.space.GraphSpace;
import com.baidu.hugegraph.structure.space.GraphSpaceReq;


public class GraphSpaceAPI extends API {

    private static final String PATH = "graphspaces";
    private static final String DELIMITER = "/";

    public GraphSpaceAPI(RestClient client) {
        super(client);
        this.path(PATH);
    }

    @Override
    protected String type() {
        return HugeType.GRAPHSPACES.string();
    }

    public GraphSpace create(GraphSpaceReq graphSpaceReq) {
        this.client.checkApiVersion("0.67", "dynamic graph add");
        RestResult result = this.client.post(this.path(), graphSpaceReq);
        return result.readObject(GraphSpace.class);
    }

    public GraphSpace get(String name) {
        RestResult result = this.client.get(this.path(), name);
        return result.readObject(GraphSpace.class);
    }

    public List<GraphSpace> list() {
        RestResult result = this.client.get(this.path());

        return result.readList(this.type(), GraphSpace.class);
    }

    public void delete(String name, String message) {
        this.client.delete(joinPath(this.path(), name),
                           ImmutableMap.of("confirm_message", message));
    }

    public GraphSpace update(GraphSpaceReq graphSpaceReq) {
        RestResult result = this.client.put(this.path(),
                                            graphSpaceReq.getName(),
                                            ImmutableMap.of("action", "update",
                                                            "update",
                                                            graphSpaceReq));

        return result.readObject(GraphSpace.class);
    }

    private static String joinPath(String path, String id) {
        return String.join(DELIMITER, path, id);
    }
}
