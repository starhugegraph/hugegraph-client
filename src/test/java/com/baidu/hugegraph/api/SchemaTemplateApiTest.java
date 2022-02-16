/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.api;

import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.exception.ServerException;
import com.baidu.hugegraph.structure.space.SchemaTemplate;
import com.baidu.hugegraph.testutil.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchemaTemplateApiTest extends BaseApiTest {
    protected static final String SCHEMATEMPLATE = "schematemmplate_2";
    protected static HugeClient client;

    @BeforeClass
    public static void beforeClass() {
        client = HugeClient.builder(BASE_URL, DEFAULT_GRAPHSPACE,
                                    GRAPH)
                           .configUser(USERNAME, PASSWORD)
                           .build();
    }

    @AfterClass
    public static void afterClass() {
        client.close();
    }

    @Test
    public void testCreateUpdateRemove() {
        SchemaTemplate schemaTemplate = new SchemaTemplate();
        schemaTemplate.name(SCHEMATEMPLATE);
        schemaTemplate.schema("schema.propertyKey(\"name\").asText()" +
                                      ".ifNotExist().create()");

        // Test Add
        client.schemaTemplateManager().createSchemaTemplate(schemaTemplate);
        Assert.assertNotNull(client.schemaTemplateManager()
                                   .getSchemaTemplate(SCHEMATEMPLATE));

        // Test update
        schemaTemplate.schema("update info");
        client.schemaTemplateManager().updateSchemaTemplate(schemaTemplate);
        Assert.assertContains("update info",
                              (String) client.schemaTemplateManager()
                                    .getSchemaTemplate(SCHEMATEMPLATE).get(
                                            "schema"));

        // Test Delete
        client.schemaTemplateManager().deleteSchemaTemplate(SCHEMATEMPLATE);
        Assert.assertThrows(ServerException.class, () -> {
            client.schemaTemplateManager().getSchemaTemplate(SCHEMATEMPLATE);
        }, (e) -> {
            Assert.assertContains(String.format("'%s' does not exist",
                                                SCHEMATEMPLATE), e.getMessage());
        });
    }
}
