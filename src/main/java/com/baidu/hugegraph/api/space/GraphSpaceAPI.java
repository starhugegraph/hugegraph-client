package com.baidu.hugegraph.api.space;

import java.util.List;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.space.GraphSpace;


public class GraphSpaceAPI extends API {

    private static final String PATH = "graphspaces";

    public GraphSpaceAPI(RestClient client) {
        super(client);
        this.path(PATH);
    }

    @Override
    protected String type() {
        return HugeType.GRAPHSPACES.string();
    }

    public GraphSpace create(GraphSpace graphSpace) {
        this.client.checkApiVersion("0.67", "dynamic graph add");
        RestResult result = this.client.post(this.path(), graphSpace);
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

    public void delete(String name) {
        this.client.delete(this.path(), name);
    }

    public GraphSpace update(GraphSpace graphSpace) {
        RestResult result = this.client.put(this.type(), graphSpace.getName(),
                                            graphSpace);

        return result.readObject(GraphSpace.class);
    }
}
