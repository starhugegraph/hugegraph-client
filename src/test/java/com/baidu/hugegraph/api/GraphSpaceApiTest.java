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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.exception.ServerException;
import com.baidu.hugegraph.structure.space.GraphSpace;
import com.baidu.hugegraph.testutil.Assert;

public class GraphSpaceApiTest extends BaseApiTest{
    protected static final String GRAPHSPACE = "graphspace_2";
    protected static final String CONFIRMMESSAGE = "I'm sure to drop the graph space";
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

    public void delete(String graphSpace) {
        client.graphSpace().deleteGraphSpace(graphSpace, CONFIRMMESSAGE);
    }

    @After
    public void after() {
        delete(GRAPHSPACE);
    }

    @Test
    public void testCreateUpdateRemove() {

        // Test Add
        GraphSpace graphSpaceInfo = new GraphSpace();
        graphSpaceInfo.setName(GRAPHSPACE).setCpuLimit(10).setMemoryLimit(100)
                      .setStorageLimit(100).setOlapNamespace("olap_namepspace")
                      .setOltpNamespace("oltp_namepsace")
                      .setStorageNamespace("storage_namespace")
                      .setDescription("test");
        client.graphSpace().createGraphSpace(graphSpaceInfo);
        Assert.assertNotNull(client.graphSpace().getGraphSpace(GRAPHSPACE));

        // Test Update
        int newCpuLimit = 100;
        graphSpaceInfo.setCpuLimit(newCpuLimit);
        client.graphSpace().updateGraphSpace(graphSpaceInfo);
        Assert.assertEquals(newCpuLimit ,
                            client.graphSpace().getGraphSpace(GRAPHSPACE).getCpuLimit());

        // Test Delete
        delete(GRAPHSPACE);
        Assert.assertThrows(ServerException.class, () -> {
            client.graphSpace().getGraphSpace(GRAPHSPACE);
        }, (e) -> {
            Assert.assertContains(String.format("'%s' does not exist", GRAPHSPACE),
                                  e.getMessage());
        });
    }
}
