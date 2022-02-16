package com.baidu.hugegraph.api.auth;

import com.baidu.hugegraph.structure.auth.HugePermission;
import com.baidu.hugegraph.structure.auth.UserManager;

public class AdminManagerAPITest extends AuthApiTest {

    public void testCreate() {
        UserManager userManagerInfo = new UserManager();
        userManagerInfo.type(HugePermission.SPACE);
        userManagerInfo.graphSpace("java");
        userManagerInfo.user("xmf1999");


        managerAPI.create(userManagerInfo);
    }
}
