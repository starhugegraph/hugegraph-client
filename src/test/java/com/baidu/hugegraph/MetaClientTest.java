package com.baidu.hugegraph;

import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.factory.DefaultHugeClientFactory;
import com.baidu.hugegraph.driver.factory.MetaHugeClientFactory;
import com.baidu.hugegraph.structure.auth.Login;
import com.baidu.hugegraph.structure.auth.LoginResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class MetaClientTest {
    protected static final String[] ETCD_ENDPOINT = {"http://127.0.0.1:2379"};
    protected static final String CLUSTER = "hg";
    protected static final String GRAPHSPACE = "DEFAULT";
    protected static final String GRAPH = "meta_hugegraph";
    protected static final String USERNAME = "admin";
    protected static final String PASSWORD = "admin";
    protected static MetaHugeClientFactory factory;
    protected static DefaultHugeClientFactory defaultFactory;
    protected static HugeClient admin;

    @BeforeClass
    public static void init() {
        factory = MetaHugeClientFactory.connect(CLUSTER, null,
                                                ETCD_ENDPOINT);
        defaultFactory =
                new DefaultHugeClientFactory(new String[]{"http://localhost:8080"});

        initAdminClient();

    }

    public static void initAdminClient() {
        HugeClient tool1 = defaultFactory.getClient(GRAPHSPACE, GRAPH);
        Login login = new Login();
        login.name(USERNAME);
        login.password(PASSWORD);
        
        tool1.auth().login(login);
        LoginResult r = tool1.auth().login(login);

        admin = defaultFactory.getClient(GRAPHSPACE, GRAPH, r.token());

        tool1.close();
    }

    @AfterClass
    public static void clean() {
        admin.close();
    }

    @Ignore
    public void testMetaClientAuth() {
        HugeClient client = factory.getClient(GRAPHSPACE, GRAPH);

        Login login = new Login();
        login.name(USERNAME);
        login.password(PASSWORD);
        LoginResult r = client.auth().login(login);

        Assert.assertNotEquals(r.token(), null);
    }

    @Test
    public void defaultClientAuth() {
        HugeClient client = defaultFactory.getClient(GRAPHSPACE, GRAPH);

        Login login = new Login();
        login.name(USERNAME);
        login.password(PASSWORD);
        LoginResult r = client.auth().login(login);

        Assert.assertNotEquals(r.token(), null);
    }
}
