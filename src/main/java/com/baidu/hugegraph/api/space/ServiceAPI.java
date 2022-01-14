package com.baidu.hugegraph.api.space;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.space.OLTPService;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

public class ServiceAPI extends API {

    private static final String PATH = "graphspaces/%s/services";
    private static final String CONFIRM_MESSAGE = "confirm_message";
    private static final String DELIMITER = "/";

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

    public Object add(OLTPService service) {
        RestResult result = this.client.post(this.path(), service);
        return result.readObject(Map.class);
    }

    public void delete(String service,  String message) {
        this.client.delete(joinPath(this.path(), service),
                           ImmutableMap.of(CONFIRM_MESSAGE, message));
    }

    public Object get(String serviceName) {
        RestResult result = this.client.get(this.path(), serviceName);

        return result.readObject(Map.class);
    }

    private static String joinPath(String path, String id) {
        return String.join(DELIMITER, path, id);
    }
}

