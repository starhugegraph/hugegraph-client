package com.baidu.hugegraph;

import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.factory.DefaultHugeClientFactory;
import com.baidu.hugegraph.structure.auth.Login;
import com.baidu.hugegraph.structure.auth.LoginResult;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class MetaClientTest {
    protected static final String GRAPHSPACE = "DEFAULT";
    protected static final String GRAPH = "meta_hugegraph";
    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    protected static DefaultHugeClientFactory defaultFactory;
    protected static HugeClient admin;

    @BeforeClass
    public static void init() {
        defaultFactory =
                new DefaultHugeClientFactory(new String[]{"http://localhost:8080"});

        initAdminClient();

    }

    public static void initAdminClient() {
        HugeClient tool1 = defaultFactory.createClient(GRAPHSPACE, GRAPH);
        Login login = new Login();
        login.name(USERNAME);
        login.password(PASSWORD);
        
        tool1.auth().login(login);
        LoginResult r = tool1.auth().login(login);

        admin = defaultFactory.createClient(GRAPHSPACE, GRAPH, r.token(),
                                            null, null);

        tool1.close();
    }

    @AfterClass
    public static void clean() {
        admin.close();
    }

    @Test
    public void defaultClientAuth() {
        HugeClient client = defaultFactory.createClient(GRAPHSPACE, GRAPH);

        Login login = new Login();
        login.name(USERNAME);
        login.password(PASSWORD);
        LoginResult r = client.auth().login(login);

        Assert.assertNotEquals(r.token(), null);
    }
}
