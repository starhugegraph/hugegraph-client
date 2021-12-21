package com.baidu.hugegraph.api.space;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.space.Service;

import java.util.List;

public class ServiceAPI<T> extends API {

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

    public <T extends Service> Object add(String name, Class<T> clazz) {
        // TODO
        return null;
    }

    public void delete(String service) {
        this.client.delete(this.path(), service);
    }

    public <T> T get(String service, Class<T> clazz) {
        RestResult result = this.client.get(this.path(), service);

        return (T) result.readObject(clazz);
    }
}

