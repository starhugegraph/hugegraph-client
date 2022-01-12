package com.baidu.hugegraph.driver;

import java.util.List;

import com.baidu.hugegraph.api.space.GraphSpaceAPI;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.structure.space.GraphSpace;
import com.baidu.hugegraph.structure.space.GraphSpaceReq;


public class GraphSpaceManager {
    private GraphSpaceAPI graphSpaceAPI;

    public GraphSpaceManager(RestClient client) {
        this.graphSpaceAPI = new GraphSpaceAPI(client);
    }

    public List<GraphSpace> listGraphSpace() {
        return this.graphSpaceAPI.list();
    }

    public GraphSpace getGraphSpace(String name) {
        return this.graphSpaceAPI.get(name);
    }

    public GraphSpace createGraphSpace(GraphSpaceReq graphSpaceReq) {
        return this.graphSpaceAPI.create(graphSpaceReq);
    }

    public void deleteGraphSpace(String name, String message) {
        this.graphSpaceAPI.delete(name, message);
    }

    public GraphSpace updateGraphSpace(GraphSpaceReq graphSpaceReq) {
        return this.graphSpaceAPI.update(graphSpaceReq);
    }
}
