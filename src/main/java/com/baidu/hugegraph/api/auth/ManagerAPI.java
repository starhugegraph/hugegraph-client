package com.baidu.hugegraph.api.auth;

import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.auth.AuthElement;
import com.baidu.hugegraph.structure.auth.HugePermission;
import com.baidu.hugegraph.structure.auth.UserManager;
import com.baidu.hugegraph.structure.constant.HugeType;

import java.util.HashMap;
import java.util.Map;

public class ManagerAPI extends AuthAPI {

    public ManagerAPI(RestClient client) {
        super(client);
    }

    public UserManager create(UserManager userManager) {
        RestResult result = this.client.post(this.path(), userManager);
        return result.readObject(UserManager.class);
    }

    public void delete(String user, HugePermission type, String graphSpace) {
        Map<String, Object> params = new HashMap<>();
        params.put("user", user);
        params.put("type", type);
        params.put("graphspace", graphSpace);
        this.client.delete(this.path(), params);
    }

    @Override
    protected String type() {
        return HugeType.MANAGER.string();
    }

    @Override
    protected Object checkCreateOrUpdate(AuthElement authElement) {
        return null;
    }
}
