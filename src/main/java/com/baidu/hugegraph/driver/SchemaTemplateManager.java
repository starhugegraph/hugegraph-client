package com.baidu.hugegraph.driver;

import java.util.List;

import com.baidu.hugegraph.api.space.SchemaTemplateAPI;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.structure.space.SchemaTemplate;


public class SchemaTemplateManager {
    private SchemaTemplateAPI schemaTemplateAPI;
    public SchemaTemplateManager(RestClient client, String graphSpace) {
        this.schemaTemplateAPI = new SchemaTemplateAPI(client, graphSpace);
    }

    public List<String> listSchemTemplate() {
        return this.schemaTemplateAPI.list();
    }

    public SchemaTemplate getSchemaTemplate(String name) {
        return this.schemaTemplateAPI.get(name);
    }

    public SchemaTemplate createSchemaTemplate(SchemaTemplate template) {
        return this.schemaTemplateAPI.create(template);
    }

    public SchemaTemplate updateSchemaTemplate(SchemaTemplate template) {
        return this.updateSchemaTemplate(template);
    }

    public void deleteSchemaTemplate(String name) {
        this.schemaTemplateAPI.delete(name);
    }
}
