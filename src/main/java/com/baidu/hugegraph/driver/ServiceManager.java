package com.baidu.hugegraph.driver;

import java.util.List;

import com.baidu.hugegraph.api.space.ServiceAPI;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.driver.factory.ServiceConfigEntity;
import com.baidu.hugegraph.structure.space.Service;

public class ServiceManager {
    private ServiceAPI serviceAPI;

    public ServiceManager(RestClient client, String graphSpace) {
        this.serviceAPI = new ServiceAPI(client, graphSpace);
    }

    public List<String> listService() {
        // TODO
        return null;
    }

    public <T extends Service> T getService(String name, Class<T> clazz) {
        return (T) this.serviceAPI.get(name, clazz);
    }

    public <T extends Service> T addService(String name, Class<T> clazz) {
       return (T) this.serviceAPI.add(name, clazz);
    }

    public void delService(String name) {
        // TODO
    }

    public void updateService(String name) {
        // TODO
    }

}
