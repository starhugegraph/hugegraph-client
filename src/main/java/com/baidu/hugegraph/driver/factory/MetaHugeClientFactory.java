package com.baidu.hugegraph.driver.factory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.baidu.hugegraph.rest.ClientException;
import com.baidu.hugegraph.structure.space.OLTPService;
import com.baidu.hugegraph.util.E;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.etcd.jetcd.ClientBuilder;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;

import com.baidu.hugegraph.exception.ServerException;
import com.baidu.hugegraph.util.JsonUtil;
import com.baidu.hugegraph.util.Log;
import com.baidu.hugegraph.driver.HugeClient;

public class MetaHugeClientFactory {

    private static final Logger LOG = Log.logger(MetaHugeClientFactory.class);

    private final MetaDriver metaDriver;

    public static final String META_PATH_DELIMETER = "/";
    public static final String META_PATH_HUGEGRAPH = "HUGEGRAPH";
    public static final String META_PATH_GRAPHSPACE = "GRAPHSPACE";
    public static final String META_PATH_SERVICE_CONF = "SERVICE_CONF";
    public static final String META_PATH_GRAPH_CONF = "GRAPH_CONF";

    public MetaHugeClientFactory(MetaDriverType type, String[] endpoints,
                                 String trustFile, String clientCertFile,
                                 String clientKeyFile) {
        if (type == null || type.equals(MetaDriverType.ETCD)) {
            if (StringUtils.isEmpty(trustFile)) {
                this.metaDriver = new EtcdMetaDriver(endpoints);
            } else {
                this.metaDriver = new EtcdMetaDriver(endpoints, trustFile,
                                                     clientCertFile,
                                                     clientKeyFile);
            }
        } else {
            throw new RuntimeException("Only ETCD SUPPORT");
        }
    }

    public static MetaHugeClientFactory connect(MetaDriverType type,
                                                String[] endpoints,
                                                String trustFile,
                                                String clientCertFile,
                                                String clientKeyFile) {
        return new MetaHugeClientFactory(type, endpoints, trustFile,
                                         clientCertFile, clientKeyFile);
    }

    public static MetaHugeClientFactory connect(MetaDriverType type,
                                                String[] endpoints) {
        return new MetaHugeClientFactory(type, endpoints, null, null, null);
    }

    protected String graphKey(String cluster, String graphSpace, String graph) {
        // HUGEGRAPH/{cluster}/GRAPHSPACE/{graphspace}/GRAPH_CONF/{graph}
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH,
                           cluster, META_PATH_GRAPHSPACE, graphSpace,
                           META_PATH_GRAPH_CONF, graph);
    }

    protected String graphServiceKey(String cluster, String graphSpace,
                                     String service) {
        // HUGEGRAPH/{cluster}/GRAPHSPACE/{graphspace}/SERVICE_CONF/{service}
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH,
                           cluster, META_PATH_GRAPHSPACE, graphSpace,
                           META_PATH_SERVICE_CONF, service);
    }

    protected String graphSpacePrefixKey(String cluster) {
        // HUGEGRAPH/{cluster}/GRAPHSPACE/CONF/
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH, cluster,
                           META_PATH_GRAPHSPACE, "CONF");
    }

    protected String clusterPrefixKey() {
        // HUGEGRAPH/
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH);
    }

    protected String graphPrefixKey(String cluster, String graphSpace) {
        // HUGEGRAPH/{cluster}/GRAPHSPACE/{graphspace}/GRAPH_CONF/
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH,
                           cluster, META_PATH_GRAPHSPACE, graphSpace,
                           META_PATH_GRAPH_CONF);
    }

    protected String graphServicePrefixKey(String cluster, String graphSpace) {
        // HUGEGRAPH/{cluster}/GRAPHSPACE/{graphspace}/SERVICE_CONF
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH,
                           cluster, META_PATH_GRAPHSPACE, graphSpace,
                           META_PATH_SERVICE_CONF);
    }

    public ImmutableSet<String> listClusters() {
        Map<String, String> scanData = this.metaDriver.scanWithPrefix(
                this.clusterPrefixKey());
        Set<String> clusters = scanData.keySet().stream()
                                       .map(s -> s.split("/")[1])
                                       .collect(Collectors.toSet());

        return ImmutableSet.copyOf(clusters);
    }

    public ImmutableSet<String> listGraphSpaces(String cluster) {

        Set<String> spaces = new HashSet<>();

        Map<String, String> kvs = this.metaDriver.scanWithPrefix(
                this.graphSpacePrefixKey(cluster));

        for (Map.Entry<String, String> entry: kvs.entrySet()) {
            String spaceName = entry.getKey().split(META_PATH_DELIMETER)[4];
            spaces.add(spaceName);
        }
        return ImmutableSet.copyOf(spaces);
    }

    public ImmutableSet<String> listGraphs(String cluster, String graphSpace) {
        Set<String> graphs = new HashSet<>();

        Map<String, String> kvs = this.metaDriver.scanWithPrefix(
                this.graphPrefixKey(cluster, graphSpace));

        for (Map.Entry<String, String> entry: kvs.entrySet()) {
            String spaceName = entry.getKey().split(META_PATH_DELIMETER)[5];
            graphs.add(spaceName);
        }
        return ImmutableSet.copyOf(graphs);
    }

    public ImmutableSet<String> listServices(String cluster, String graphSpace) {
        Set<String> services = new HashSet<>();

        Map<String, String> kvs = this.metaDriver.scanWithPrefix(
                this.graphServicePrefixKey(cluster, graphSpace));

        for (Map.Entry<String, String> entry: kvs.entrySet()) {
            String serviceName = entry.getKey().split(META_PATH_DELIMETER)[5];
            services.add(serviceName);
        }
        return ImmutableSet.copyOf(services);
    }

    public ImmutableSet<ImmutableList<String>> listExtendServices(String cluster) {
        // Get All OLTP Service
        Set<ImmutableList<String>> services = new HashSet<>();

        ImmutableSet<String> graphSpaces = this.listGraphSpaces(cluster);

        for (String graphSpace: graphSpaces) {
            Map<String, String> kvs = this.metaDriver.scanWithPrefix(
                    this.graphServicePrefixKey(cluster, graphSpace));

            for (Map.Entry<String, String> entry: kvs.entrySet()) {
                String serviceName =
                        entry.getKey().split(META_PATH_DELIMETER)[5];
                OLTPService s
                        = JsonUtil.fromJson(entry.getValue(),
                                            OLTPService.class);

                if (s.getUrls().size() > 0){
                    services.add(ImmutableList.of(graphSpace, serviceName));
                }
            }
        }
        return ImmutableSet.copyOf(services);
    }

    public ImmutableSet<ImmutableList<String>> listExtendGraphs(String cluster) {

        Set<ImmutableList<String>> graphs = new HashSet<>();

        ImmutableSet<String> graphSpaces = this.listGraphSpaces(cluster);

        for (String graphSpace: graphSpaces) {
            this.listGraphs(cluster, graphSpace).forEach((g) -> {
                graphs.add(ImmutableList.of(graphSpace, g));
            });
        }

        return ImmutableSet.copyOf(graphs);
    }

    public Map<String, String> getGraphConfig(String cluster,
                                               String graphspace,
                                              String graph) {
        String value = this.metaDriver.get(this.graphKey(cluster, graphspace,
                                                         graph));
        if (value != null) {
            return JsonUtil.fromJson(value, Map.class);
        }
        return null;
    }

    public OLTPService getServiceConfig(String cluster,
                                                String graphspace,
                                              String service) {
        String value =
                this.metaDriver.get(this.graphServiceKey(cluster, graphspace,
                                                         service));
        if (value != null) {
            return JsonUtil.fromJson(value, OLTPService.class);
        }
        return null;
    }

    protected HugeClient createClientWithService(String cluster,
                                                 String graphSpace,
                                                 String service, String token,
                                                 String username, String password) {

        E.checkArgument(cluster != null && graphSpace != null & service != null,
                        "createClientWithService: cluster & graphspace must not null");

        OLTPService serviceConfig = getServiceConfig(cluster, graphSpace,
                                                     service);

        LOG.info("create client with graphspace:{}, service:{}, service " +
                         "config: {} ", graphSpace, service, serviceConfig);

        DefaultHugeClientFactory defaultFactory = new DefaultHugeClientFactory(
                serviceConfig.getUrls().toArray(new String[0]));

        return defaultFactory.createClient(graphSpace, null, token, username,
                                           password);
    }

    public HugeClient createUnauthClient(String cluster, String graphSpace,
                                            String graph) {
        E.checkArgument(cluster != null, "create unauth client: cluster must " +
                "not null");

        return createClient(cluster, graphSpace, graph, null, null, null);
    }

    public HugeClient createAuthClient(String cluster, String graphSpace,
                                       String graph, String token,
                                       String username, String password) {
        E.checkArgument(cluster != null,
                        "create auth client: cluster must not null");

        E.checkArgument(token != null || (username != null && password != null),
                        "create auth client: token must not null or " +
                                "username/password must not null");

        return createClient(cluster, graphSpace, graph, token, username, password);
    }

    protected String randomService(String cluster, String graphSpace,
                                   String graph) {
        String serviceName = null;
        if (StringUtils.isEmpty(graphSpace)) {
            ImmutableSet<ImmutableList<String>> services
                    = this.listExtendServices(cluster);

            E.checkArgument(services.size() > 0, "No OLTP Service Exist " +
                    "Under" + cluster);

            int r1 = (int) (Math.random() * services.size());
            ImmutableList<String> rand = services.asList().get(r1);
            graphSpace = rand.get(0);
            serviceName = rand.get(1);
        }

        if (!StringUtils.isEmpty(graphSpace)) {
            if (!StringUtils.isEmpty(graph)) {
                Map<String, String> graphConfig = getGraphConfig(cluster,
                                                                 graphSpace,
                                                                 graph);
                if (graphConfig != null && !StringUtils.isEmpty(
                        graphConfig.get("service"))) {
                    serviceName = graphConfig.get("service");
                }
            }

            if (StringUtils.isEmpty(serviceName)) {
                ImmutableSet<String> serviceNames
                        = listServices(cluster, graphSpace);
                E.checkArgument(serviceNames.size() > 0, "No service under " +
                        "cluster: %s", cluster);
                int r2 = (int) (Math.random() * serviceNames.size());
                serviceName = serviceNames.asList().get(r2);
            }
        }

        LOG.info("create client with graphSpace:{}, serviceName:{}",
                 graphSpace, serviceName);

        return serviceName;
    }

    protected HugeClient createClient(String cluster, String graphSpace,
                                      String graph, String token,
                                      String username, String password) {

        // Choose one service as server instance
        String serviceName = randomService(cluster, graphSpace, graph);
        return createClientWithService(cluster, graphSpace, serviceName,
                                       token, username, password);
    }

    public enum MetaDriverType {
        ETCD("ETCD"),
        PD("PD");

        private String value;

        MetaDriverType(String value) {
            this.setValue(value);
        }

        public void setValue(String value) {
            this.value = value.toUpperCase();
        }

        public String getValue() {
            return value;
        }

        public boolean equals(MetaDriverType type) {
            return this.value.equalsIgnoreCase(type.getValue());
        }

    }

    protected interface MetaDriver {
        String get(String key);

        Map<String, String> scanWithPrefix(String prefix);
    }

    protected class EtcdMetaDriver implements MetaDriver{
        private Client client;

        public EtcdMetaDriver(String[] endpoints) {
            this.client = Client.builder()
                                .endpoints(endpoints)
                                .build();
        }

        public EtcdMetaDriver(String [] endpoints, String trustFile,
                              String clientCertFile, String clientKeyFile) {
            ClientBuilder builder = Client.builder()
                                          .endpoints(endpoints);

            SslContext sslContext = openSslContext(trustFile, clientCertFile,
                                                   clientKeyFile);

            this.client = builder.sslContext(sslContext).build();
        }

        protected SslContext openSslContext(String trustFile,
                                                String clientCertFile,
                                                String clientKeyFile) {
            SslContext ssl;
            try {
                File trustManagerFile = FileUtils.getFile(trustFile);
                File keyCertChainFile = FileUtils.getFile(clientCertFile);
                File keyFile = FileUtils.getFile(clientKeyFile);
                ApplicationProtocolConfig alpn = new ApplicationProtocolConfig(
                        ApplicationProtocolConfig.Protocol.ALPN,
                        ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,

                        ApplicationProtocolConfig.SelectedListenerFailureBehavior
                                .ACCEPT,
                        ApplicationProtocolNames.HTTP_2);

                ssl = SslContextBuilder.forClient()
                                       .applicationProtocolConfig(alpn)
                                       .sslProvider(SslProvider.OPENSSL)
                                       .trustManager(trustManagerFile)
                                       .keyManager(keyCertChainFile, keyFile)
                                       .build();
            } catch (Exception e) {
                throw new ClientException("Failed to open ssl context", e);
            }
            return ssl;
        }


        @Override
        public String get(String key) {
            List<KeyValue> keyValues;
            KV kvClient = this.client.getKVClient();
            try {
                keyValues = kvClient.get(ByteSequence.from(key.getBytes()))
                                    .get().getKvs();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(String.format("Failed to get key '%s'" +
                        " from etcd, %s", key, e));
            }

            if (keyValues.size() > 0) {
                return keyValues.get(0).getValue().toString(Charset.defaultCharset());
            }

            return null;
        }

        protected ByteSequence toByteSequence(String content) {
            return ByteSequence.from(content.getBytes());
        }

        @Override
        public Map<String, String> scanWithPrefix(String prefix) {
            GetOption getOption = GetOption.newBuilder()
                                           .withPrefix(toByteSequence(prefix))
                                           .build();
            GetResponse response;
            try {
                response = this.client.getKVClient().get(toByteSequence(prefix),
                                                         getOption).get();
            } catch (InterruptedException | ExecutionException e) {
                LOG.error("Failed to scan etcd with prefix: {}", prefix, e);
                throw new ServerException("Failed to scan etcd with prefix: " +
                                                prefix + " " + e);
            }
            int size = (int) response.getCount();
            Map<String, String> keyValues = new HashMap<>(size);
            for (KeyValue kv : response.getKvs()) {
                keyValues.put(kv.getKey().toString(Charset.defaultCharset()),
                              kv.getValue().toString(Charset.defaultCharset()));
            }
            return keyValues;
        }
    }

    protected class PdMetaDriver implements MetaDriver{
        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public Map<String, String> scanWithPrefix(String prefix) {
            return null;
        }
    }
}
