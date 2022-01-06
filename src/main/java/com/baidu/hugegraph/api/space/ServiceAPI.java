package com.baidu.hugegraph.api.space;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.space.OLTPService;

import java.util.List;

public class ServiceAPI extends API {

    private static final String PATH = "graphspaces/%s/services";

    public ServiceAPI(RestClient client, String graphSpace) {
        super(client);
        this.path(String.format(PATH, graphSpace));
    }

    @Override
    protected String type() {
        return HugeType.SERVICES.string();
    }
    
    public List<String> list() {
        RestResult result = this.client.get(this.path());

        return result.readList(this.type(), String.class);
    }

    public OLTPService add(OLTPService service) {
        RestResult result = this.client.post(this.path(), service);
        return result.readObject(OLTPService.class);
    }

    public void delete(String service) {
        this.client.delete(this.path(), service);
    }

    public OLTPService get(String serviceName) {
        RestResult result = this.client.get(this.path(), serviceName);

        return result.readObject(OLTPService.class);
    }
}

