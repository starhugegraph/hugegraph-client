package com.baidu.hugegraph.driver.factory;

import java.util.Set;

public class ServiceConfigEntity {

    private String name;
    private ServiceType type;
    private String description;

    private Set<String> urls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(
            ServiceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public boolean isOLTP() {
        return this.type == ServiceType.OLTP;
    }

    public enum ServiceType {
        OLTP,
        OLAP,
        STORAGE
    }
}
