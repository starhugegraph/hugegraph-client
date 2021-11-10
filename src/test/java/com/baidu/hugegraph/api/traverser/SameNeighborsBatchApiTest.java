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

package com.baidu.hugegraph.api.traverser;

import com.baidu.hugegraph.structure.constant.Direction;
import com.baidu.hugegraph.structure.constant.T;
import com.baidu.hugegraph.structure.graph.Vertex;
import com.baidu.hugegraph.structure.traverser.SameNeighbors;
import com.baidu.hugegraph.structure.traverser.SameNeighborsBatchRequest;
import com.baidu.hugegraph.testutil.Assert;
import com.google.common.collect.ImmutableList;
import org.junit.BeforeClass;
import org.junit.Test;

public class SameNeighborsBatchApiTest extends TraverserApiTest {

    @BeforeClass
    public static void initShortestPathGraph() {
        schema().vertexLabel("node")
                .useCustomizeNumberId()
                .ifNotExist()
                .create();

        schema().edgeLabel("link")
                .sourceLabel("node").targetLabel("node")
                .ifNotExist()
                .create();
        schema().edgeLabel("relateTo")
                .sourceLabel("node").targetLabel("node")
                .ifNotExist()
                .create();

        Vertex v1 = graph().addVertex(T.label, "node", T.id, 1);
        Vertex v2 = graph().addVertex(T.label, "node", T.id, 2);
        Vertex v3 = graph().addVertex(T.label, "node", T.id, 3);
        Vertex v4 = graph().addVertex(T.label, "node", T.id, 4);
        Vertex v5 = graph().addVertex(T.label, "node", T.id, 5);
        Vertex v6 = graph().addVertex(T.label, "node", T.id, 6);
        Vertex v7 = graph().addVertex(T.label, "node", T.id, 7);
        Vertex v8 = graph().addVertex(T.label, "node", T.id, 8);
        Vertex v9 = graph().addVertex(T.label, "node", T.id, 9);
        Vertex v10 = graph().addVertex(T.label, "node", T.id, 10);

        v1.addEdge("link", v3);
        v2.addEdge("link", v3);
        v4.addEdge("link", v1);
        v4.addEdge("link", v2);

        v1.addEdge("relateTo", v5);
        v2.addEdge("relateTo", v5);
        v6.addEdge("relateTo", v1);
        v6.addEdge("relateTo", v2);

        v1.addEdge("link", v7);
        v8.addEdge("link", v1);
        v2.addEdge("link", v9);
        v10.addEdge("link", v2);
    }

    @Test
    public void testSameNeighborsBatchBatch() {
        SameNeighborsBatchRequest.Builder builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(7, 9);
        SameNeighborsBatchRequest request = builder.build();
        SameNeighbors neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(4, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3, 4, 5, 6)));
        Assert.assertTrue(neighbors.sameNeighbors.get(1).containsAll(ImmutableList.of(1, 2)));
    }

    @Test
    public void testSameNeighborsWithDirection() {
        // OUT
        SameNeighborsBatchRequest.Builder builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 6);
        SameNeighborsBatchRequest request = builder.build();
        builder.direction(Direction.OUT);
        SameNeighbors neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3, 5)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(1, 2)));

        // IN
        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(5, 9);
        request = builder.build();
        builder.direction(Direction.IN);
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(4, 6)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(2)));
    }

    @Test
    public void testSameNeighborsWithLabel() {
        SameNeighborsBatchRequest.Builder builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 6);
        SameNeighborsBatchRequest request = builder.build();
        builder.direction(Direction.BOTH);
        builder.label("link");
        SameNeighbors neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3, 4)));
        Assert.assertTrue(neighbors.sameNeighbors.get(1).containsAll(ImmutableList.of(1, 2)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 8);
        request = builder.build();
        builder.direction(Direction.OUT);
        builder.label("link");
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(1)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(3, 9);
        request = builder.build();
        builder.direction(Direction.IN);
        builder.label("link");
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(4)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(2)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(3, 9);
        request = builder.build();
        builder.direction(Direction.IN);
        builder.label("link");
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(4)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(2)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(5, 6);
        request = builder.build();
        builder.direction(Direction.BOTH);
        builder.label("relateTo");
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(5, 6)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(1, 2)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(5, 6);
        request = builder.build();
        builder.direction(Direction.OUT);
        builder.label("relateTo");
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(5)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(5, 6);
        request = builder.build();
        builder.direction(Direction.IN);
        builder.label("relateTo");
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(6)));
    }

    @Test
    public void testSameNeighborsWithDegree() {
        SameNeighborsBatchRequest.Builder builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 6);
        SameNeighborsBatchRequest request = builder.build();
        builder.direction(Direction.OUT);
        builder.maxDegree(6L);
        SameNeighbors neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3, 5)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(1, 2)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 6);
        request = builder.build();
        builder.direction(Direction.OUT);
        builder.maxDegree(1L);
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(1, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3)));
        Assert.assertTrue(neighbors.sameNeighbors.get(2).containsAll(ImmutableList.of(1)));
    }

    @Test
    public void testSameNeighborsWithLimit() {
        SameNeighborsBatchRequest.Builder builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 6);
        SameNeighborsBatchRequest request = builder.build();
        builder.direction(Direction.BOTH);
        builder.maxDegree(6);
        builder.label("link");
        SameNeighbors neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3, 4)));
        Assert.assertTrue(neighbors.sameNeighbors.get(1).containsAll(ImmutableList.of(1, 2)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 6);
        builder.build();
        builder.direction(Direction.BOTH);
        builder.maxDegree(6);
        builder.label("link");
        builder.limit(2L);
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3, 4)));
        Assert.assertTrue(neighbors.sameNeighbors.get(1).containsAll(ImmutableList.of(1, 2)));

        builder = SameNeighborsBatchRequest.builder();
        builder.vertex(1, 2);
        builder.vertex(3, 4);
        builder.vertex(4, 6);
        builder.build();
        builder.direction(Direction.BOTH);
        builder.maxDegree(6);
        builder.label("link");
        builder.limit(1L);
        neighbors = sameNeighborsBatchAPI.post(request);
        Assert.assertEquals(3, neighbors.sameNeighbors.size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(0).size());
        Assert.assertEquals(2, neighbors.sameNeighbors.get(1).size());
        Assert.assertEquals(0, neighbors.sameNeighbors.get(2).size());

        Assert.assertTrue(neighbors.sameNeighbors.get(0).containsAll(ImmutableList.of(3)));
        Assert.assertTrue(neighbors.sameNeighbors.get(1).containsAll(ImmutableList.of(1)));
    }
}
