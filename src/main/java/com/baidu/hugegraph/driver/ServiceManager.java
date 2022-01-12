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

    public Object getService(String name) {
        return this.serviceAPI.get(name);
    }

    public Object addService(OLTPService service) {
       return this.serviceAPI.add(service);
    }

    public void delService(String name, String message) {
        this.serviceAPI.delete(name, message);
    }

    public Object updateService(OLTPService service) {
        delService(service.getName(), "I'm sure to delete the service");
        return this.serviceAPI.add(service);
    }
}
