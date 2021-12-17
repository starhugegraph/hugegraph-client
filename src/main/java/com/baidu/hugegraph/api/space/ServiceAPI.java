package com.baidu.hugegraph.api.space;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.structure.constant.HugeType;

public class ServiceAPI extends API {

    private static final String PATH = "services";

    public ServiceAPI(RestClient client) {
        super(client);
        this.path(PATH);
    }

    @Override
    protected String type() {
        return HugeType.SERVICES.string();
    }
}

