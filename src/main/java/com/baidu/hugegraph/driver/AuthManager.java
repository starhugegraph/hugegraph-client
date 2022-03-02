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

package com.baidu.hugegraph.driver;

import java.util.List;

import com.baidu.hugegraph.api.auth.AccessAPI;
import com.baidu.hugegraph.api.auth.BelongAPI;
import com.baidu.hugegraph.api.auth.GroupAPI;
import com.baidu.hugegraph.api.auth.KGLoginAPI;
import com.baidu.hugegraph.api.auth.LoginAPI;
import com.baidu.hugegraph.api.auth.LogoutAPI;
import com.baidu.hugegraph.api.auth.ManagerAPI;
import com.baidu.hugegraph.api.auth.TargetAPI;
import com.baidu.hugegraph.api.auth.TokenAPI;
import com.baidu.hugegraph.api.auth.UserAPI;
import com.baidu.hugegraph.client.RestClient;
import com.baidu.hugegraph.structure.auth.Access;
import com.baidu.hugegraph.structure.auth.Belong;
import com.baidu.hugegraph.structure.auth.Group;
import com.baidu.hugegraph.structure.auth.HugePermission;
import com.baidu.hugegraph.structure.auth.KGLogin;
import com.baidu.hugegraph.structure.auth.KGLoginResult;
import com.baidu.hugegraph.structure.auth.Login;
import com.baidu.hugegraph.structure.auth.LoginResult;
import com.baidu.hugegraph.structure.auth.Target;
import com.baidu.hugegraph.structure.auth.TokenPayload;
import com.baidu.hugegraph.structure.auth.User;
import com.baidu.hugegraph.structure.auth.User.UserRole;
import com.baidu.hugegraph.structure.auth.UserManager;

public class AuthManager {

    private final TargetAPI targetAPI;
    private final GroupAPI groupAPI;
    private final UserAPI userAPI;
    private final AccessAPI accessAPI;
    private final BelongAPI belongAPI;
    private final LoginAPI loginAPI;
    private final LogoutAPI logoutAPI;
    private final TokenAPI tokenAPI;
    private final KGLoginAPI kgLoginAPI;
    private final ManagerAPI managerAPI;

    public AuthManager(RestClient client, String graphSpace) {
        this.targetAPI = new TargetAPI(client, graphSpace);
        this.groupAPI = new GroupAPI(client, graphSpace);
        this.userAPI = new UserAPI(client);
        this.accessAPI = new AccessAPI(client, graphSpace);
        this.belongAPI = new BelongAPI(client, graphSpace);
        this.loginAPI = new LoginAPI(client);
        this.logoutAPI = new LogoutAPI(client);
        this.tokenAPI = new TokenAPI(client);
        this.kgLoginAPI = new KGLoginAPI(client);
        this.managerAPI = new ManagerAPI(client);
    }

    public List<Target> listTargets() {
        return this.listTargets(-1);
    }

    public List<Target> listTargets(int limit) {
        return this.targetAPI.list(limit);
    }

    public Target getTarget(Object id) {
        return this.targetAPI.get(id);
    }

    public Target createTarget(Target target) {
        return this.targetAPI.create(target);
    }

    public Target updateTarget(Target target) {
        return this.targetAPI.update(target);
    }

    public void deleteTarget(Object id) {
        this.targetAPI.delete(id);
    }

    public List<Group> listGroups() {
        return this.listGroups(-1);
    }

    public List<Group> listGroups(int limit) {
        return this.groupAPI.list(limit);
    }

    public Group getGroup(Object id) {
        return this.groupAPI.get(id);
    }

    public Group createGroup(Group group) {
        return this.groupAPI.create(group);
    }

    public Group updateGroup(Group group) {
        return this.groupAPI.update(group);
    }

    public void deleteGroup(Object id) {
        this.groupAPI.delete(id);
    }

    public List<User> listUsers() {
        return this.listUsers(-1);
    }

    public List<User> listUsers(int limit) {
        return this.userAPI.list(limit);
    }

    public User getUser(Object id) {
        return this.userAPI.get(id);
    }

    public UserRole getUserRole(Object id) {
        return this.userAPI.getUserRole(id);
    }

    public User createUser(User user) {
        return this.userAPI.create(user);
    }

    public User updateUser(User user) {
        return this.userAPI.update(user);
    }

    public void deleteUser(Object id) {
        this.userAPI.delete(id);
    }

    public List<Access> listAccesses() {
        return this.listAccesses(-1);
    }

    public List<Access> listAccesses(int limit) {
        return this.accessAPI.list(null, null, limit);
    }

    public List<Access> listAccessesByGroup(Object group, int limit) {
        return this.accessAPI.list(group, null, limit);
    }

    public List<Access> listAccessesByTarget(Object target, int limit) {
        return this.accessAPI.list(null, target, limit);
    }

    public Access getAccess(Object id) {
        return this.accessAPI.get(id);
    }

    public Access createAccess(Access access) {
        return this.accessAPI.create(access);
    }

    public Access updateAccess(Access access) {
        return this.accessAPI.update(access);
    }

    public void deleteAccess(Object id) {
        this.accessAPI.delete(id);
    }

    public List<Belong> listBelongs() {
        return this.listBelongs(-1);
    }

    public List<Belong> listBelongs(int limit) {
        return this.belongAPI.list(null, null, limit);
    }

    public List<Belong> listBelongsByUser(Object user, int limit) {
        return this.belongAPI.list(user, null, limit);
    }

    public List<Belong> listBelongsByGroup(Object group, int limit) {
        return this.belongAPI.list(null, group, limit);
    }

    public Belong getBelong(Object id) {
        return this.belongAPI.get(id);
    }

    public Belong createBelong(Belong belong) {
        return this.belongAPI.create(belong);
    }

    public Belong updateBelong(Belong belong) {
        return this.belongAPI.update(belong);
    }

    public void deleteBelong(Object id) {
        this.belongAPI.delete(id);
    }

    public void deleteAll() {
        for (Belong belong : this.listBelongs()) {
            this.deleteBelong(belong.id());
        }
        for (Access access : this.listAccesses()) {
            this.deleteAccess(access.id());
        }

        for (User user : this.listUsers()) {
            if (user.name().equals("admin")) {
                continue;
            }
            this.deleteUser(user.id());
        }
        for (Group group : this.listGroups()) {
            this.deleteGroup(group.id());
        }
        for (Target target : this.listTargets()) {
            this.deleteTarget(target.id());
        }
    }

    public LoginResult login(Login login) {
        return this.loginAPI.login(login);
    }

    public void logout() {
        this.logoutAPI.logout();
    }

    public TokenPayload verifyToken() {
        return this.tokenAPI.verifyToken();
    }

    public KGLoginResult kgLogin(KGLogin kgLogin) {
        return this.kgLoginAPI.kgLogin(kgLogin);
    }

    public UserManager addSuperAdmin(String user) {

        UserManager userManager = new UserManager();
        userManager.type(HugePermission.ADMIN);
        userManager.user(user);

        return this.managerAPI.create(userManager);
    }

    public UserManager addSpaceAdmin(String user, String graphSpace) {

        UserManager userManager = new UserManager();
        userManager.type(HugePermission.SPACE);
        userManager.graphSpace(graphSpace);
        userManager.user(user);

        return this.managerAPI.create(userManager);
    }

    public void delSuperAdmin(String user) {
        this.managerAPI.delete(user, HugePermission.ADMIN, null);
    }

    public void delSpaceAdmin(String user, String graphSpace) {
        this.managerAPI.delete(user, HugePermission.SPACE, graphSpace);
    }

    public List<String> listSpaceAdmin(String graphSpace) {
        return this.managerAPI.list(HugePermission.SPACE, graphSpace);
    }

    public List<String> listSuperAdmin() {
        return this.managerAPI.list(HugePermission.ADMIN, null);
    }

    public boolean isSuperAdmin() {
        return this.managerAPI.checkPermission(HugePermission.ADMIN, null);
    }

    public boolean isSpaceAdmin(String graphSpace) {
        return this.managerAPI.checkPermission(HugePermission.SPACE, graphSpace);
    }
}
