package com.baidu.hugegraph.api.space;

import java.util.List;

import com.baidu.hugegraph.api.API;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.rest.RestResult;
import com.baidu.hugegraph.structure.constant.HugeType;
import com.baidu.hugegraph.structure.space.SchemaTemplate;

public class SchemaTemplateAPI extends API {
    private static final String PATH = "graphspaces/%s/schematemplates";

    public SchemaTemplateAPI(RestClient client, String graphSpace) {
        super(client);
        this.path(String.format(PATH, graphSpace));
    }

    @Override
    protected String type() {
        return HugeType.SCHEMATEMPLATES.string();
    }

    public SchemaTemplate create(SchemaTemplate template) {
        RestResult result = this.client.post(this.path(), template);
        return result.readObject(SchemaTemplate.class);
    }

    public List<String> list() {
        RestResult result = this.client.get(this.path());
        return result.readList(this.path(), String.class);
    }

    public SchemaTemplate get(String name) {
        RestResult result = this.client.get(this.path(), name);
        return result.readObject(SchemaTemplate.class);
    }

    public void delete(String name) {
        this.client.delete(this.path(), name);
    }

    public SchemaTemplate update(SchemaTemplate template) {
        RestResult result = this.client.put(this.type(), template.name(),
                                            template);

        return result.readObject(SchemaTemplate.class);
    }
}
