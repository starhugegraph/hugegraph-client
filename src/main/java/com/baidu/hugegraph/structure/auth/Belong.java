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

package com.baidu.hugegraph.structure.auth;

import java.util.Date;

import com.baidu.hugegraph.structure.constant.HugeType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Belong extends AuthElement {

    @JsonProperty("graphspace")
    protected String graphSpace;
    @JsonProperty("user")
    protected Object user;
    @JsonProperty("group")
    protected Object group;
    @JsonProperty("belong_description")
    protected String description;

    @JsonProperty("belong_create")
    @JsonFormat(pattern = DATE_FORMAT)
    protected Date create;
    @JsonProperty("belong_update")
    @JsonFormat(pattern = DATE_FORMAT)
    protected Date update;
    @JsonProperty("belong_creator")
    protected String creator;

    @Override
    public String type() {
        return HugeType.BELONG.string();
    }

    @Override
    public Date createTime() {
        return this.create;
    }

    @Override
    public Date updateTime() {
        return this.update;
    }

    @Override
    public String creator() {
        return this.creator;
    }

    public String graphSpace() {
        return this.graphSpace;
    }

    public void graphSpace(String graphSpace) {
        this.graphSpace = graphSpace;
    }

    public Object user() {
        return this.user;
    }

    public void user(Object user) {
        if (user instanceof User) {
            user = ((User) user).id();
        }
        this.user = user;
    }

    public Object group() {
        return this.group;
    }

    public void group(Object group) {
        if (group instanceof Group) {
            group = ((Group) group).id();
        }
        this.group = group;
    }

    public String description() {
        return this.description;
    }

    public void description(String description) {
        this.description = description;
    }

    public BelongReq switchReq() {
        return new BelongReq(this);
    }

    @JsonIgnoreProperties({"graphspace"})
    public static class BelongReq extends Belong {

        public BelongReq(Belong belong) {
            this.id = belong.id();
            this.user = belong.user();
            this.group = belong.group();
            this.description = belong.description();
            this.update = belong.updateTime();
            this.create = belong.createTime();
            this.creator = belong.creator();
        }
    }
}
