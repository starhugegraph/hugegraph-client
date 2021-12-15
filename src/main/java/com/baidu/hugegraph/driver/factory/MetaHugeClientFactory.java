package com.baidu.hugegraph.driver.factory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.baidu.hugegraph.util.E;
import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.util.Strings;
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

    public MetaHugeClientFactory(MetaDriverType type, String... endpoints) {
        if (type == null || type.equals(MetaDriverType.ETCD)) {
            this.metaDriver = new EtcdMetaDriver(endpoints);
        } else {
            throw new RuntimeException("Only ETCD SUPPORT");
        }
    }

    public static MetaHugeClientFactory connect(MetaDriverType type,
                                                String... args) {
        MetaHugeClientFactory factory = new MetaHugeClientFactory(type, args);
        return factory;
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

    public Map<String, Object> getGraphConfig(String cluster, String graphspace,
                                              String graph) {
        String value = this.metaDriver.get(this.graphKey(cluster, graphspace,
                                                         graph));
        if (value != null) {
            return JsonUtil.fromJson(value, Map.class);
        }
        return null;
    }

    public Map<String, Object> getServiceConfig(String cluster,
                                                String graphspace,
                                              String service) {
        String value =
                this.metaDriver.get(this.graphServiceKey(cluster, graphspace,
                                                         service));
        if (value != null) {
            return JsonUtil.fromJson(value, Map.class);
        }
        return null;
    }

    protected ImmutableSet<String> getServiceUrls(String cluster,
                                                  String graphSpace,
                                                  String serviceName) {
        Map<String, Object> graphService =
                this.getServiceConfig(cluster, graphSpace, serviceName);
        if (graphService == null) {
            LOG.warn("{}/{} 's service info is null",
                     graphSpace, serviceName);
            return null;
        }

        LOG.debug("Get {}/{} 's serviceConfig From Meta: {}",
                  graphSpace, serviceName, graphService);

        ImmutableSet<String> urls = ImmutableSet.copyOf(
                (List<String>) graphService.get("urls"));
        if (urls == null || urls.size() == 0) {
            LOG.warn("{}/{} 's urls is empty", graphSpace, serviceName);
            return null;
        }

        return urls;
    }

    protected HugeClient createClientWithService(String cluster,
                                               String graphSpace,
                                   String service, String token) {

        E.checkArgument(cluster != null && graphSpace != null & service != null,
                        "createClientWithService: cluster & graphspace must not null");

        ImmutableSet<String> urls = this.getServiceUrls(cluster, graphSpace,
                                                        service);

        LOG.info("Host of {}/{}: {} ", graphSpace, service, urls);

        DefaultHugeClientFactory defaultFactory = new DefaultHugeClientFactory(
                (String[]) urls.toArray());

        return defaultFactory.createClient(graphSpace, null, token);
    }

    public HugeClient createUnauthClient(String cluster, String graphSpace,
                                            String graph) {
        E.checkArgument(cluster != null && graphSpace != null, "create unauth" +
                " client: cluster & graphspace must not null");

        return createClient(cluster, graphSpace, graph, null);
    }

    public HugeClient createAuthClient(String cluster, String graphSpace,
                                            String graph, String token) {
        E.checkArgument(cluster != null && graphSpace != null && token != null,
                        "create auth client: cluster & graphspace & token " +
                                "must not null");

        return createClient(cluster, graphSpace, graph, token);
    }

    protected HugeClient createClient(String cluster, String graphSpace,
                                           String graph, String token) {

        String serviceName = null;
        if (!Strings.isEmpty(graph)) {
            Map<String, Object> graphConfig = this.getGraphConfig(cluster,
                                                                  graphSpace,
                                                                  graph);
            if (graphConfig == null) {
                LOG.warn("{}/{} 's graphConfig is null", graphSpace, graph);
                return null;
            }

            LOG.debug("Get {}/{} 's graphConfig From Meta: {}",
                      graphSpace, graph, graphConfig);
            serviceName = (String) graphConfig.get("service");

            if (Strings.isEmpty(serviceName)) {
                LOG.warn("{}/{} 's servicename is null", graphSpace, graph);
            }
        }

        if (Strings.isEmpty(serviceName)) {
            ImmutableSet<String> services = this.listServices(cluster,
                                                              graphSpace);
            serviceName = services.asList()
                                  .get((int) (Math.random() * services.size()));
        }


        return this.createClientWithService(cluster, graphSpace, serviceName,
                                            token);
    }


    public ImmutableSet<String> listClusters() {
        Map<String, String> scanData = this.metaDriver.scanWithPrefix(
                this.clusterPrefixKey());
        Set<String> clusters = scanData.entrySet().stream()
                                       .map((e) -> e.getKey().split("/")[1])
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
            String spaceName = entry.getKey().split(META_PATH_DELIMETER)[5];
            services.add(spaceName);
        }
        return ImmutableSet.copyOf(services);
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
        public String get(String key);

        Map<String, String> scanWithPrefix(String prefix);
    }

    protected class EtcdMetaDriver implements MetaDriver{
        private Client client;

        public EtcdMetaDriver(String... endpoints) {
            this.client = Client.builder()
                                .endpoints((String[]) endpoints)
                                .build();
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
                        " from etcd", key, e));
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
                throw new ServerException("Failed to scan etcd with prefix " +
                                                prefix, e);
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
