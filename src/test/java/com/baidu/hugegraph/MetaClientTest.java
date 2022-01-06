package com.baidu.hugegraph;

import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.driver.factory.DefaultHugeClientFactory;
import com.baidu.hugegraph.driver.factory.MetaHugeClientFactory;
import com.baidu.hugegraph.structure.auth.Login;
import com.baidu.hugegraph.structure.auth.LoginResult;
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
        // factory = MetaHugeClientFactory.connect(null,
        //                                         ETCD_ENDPOINT);
        factory = MetaHugeClientFactory
                .connect(null, ETCD_ENDPOINT,
                         "/Users/wenchuanbo/workspace/cfssl/ca.pem",
                         "/Users/wenchuanbo/workspace/cfssl/client.pem",
                         "/Users/wenchuanbo/workspace/cfssl/client-key-pkcs8.pem");
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
    public void testMetaClientAuth() {
        HugeClient client = factory.createUnauthClient(CLUSTER, GRAPHSPACE,
                                                     GRAPH);

        Login login = new Login();
        login.name(USERNAME);
        login.password(PASSWORD);
        LoginResult r = client.auth().login(login);

        Assert.assertNotEquals(r.token(), null);
    }

    @Test
    public void testMetaFactoryListSpace() {
        factory.listClusters().stream().forEach(System.out::println);
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
