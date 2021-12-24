package com.baidu.hugegraph.driver;

import java.util.List;

import com.baidu.hugegraph.api.space.ServiceAPI;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.structure.space.OLTPService;

public class ServiceManager {
    private ServiceAPI serviceAPI;

    public ServiceManager(RestClient client, String graphSpace) {
        this.serviceAPI = new ServiceAPI(client, graphSpace);
    }

    public List<String> listService() {
        return serviceAPI.list();
    }

    public OLTPService getService(String name) {
        return this.serviceAPI.get(name);
    }

    public OLTPService addService(OLTPService service) {
       return this.serviceAPI.add(service);
    }

    public void delService(String name) {
        this.serviceAPI.delete(name);
    }

    public OLTPService updateService(OLTPService service) {
        return this.serviceAPI.add(service);
    }
}
