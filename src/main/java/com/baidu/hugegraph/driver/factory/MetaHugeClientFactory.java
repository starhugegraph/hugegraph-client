package com.baidu.hugegraph.driver.factory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;

import com.baidu.hugegraph.util.JsonUtil;
import com.baidu.hugegraph.util.Log;
import com.baidu.hugegraph.driver.HugeClient;
import com.baidu.hugegraph.util.E;

public class MetaHugeClientFactory extends AbstractHugeClientFactory {

    private static final Logger LOG = Log.logger(MetaHugeClientFactory.class);

    private String cluster;
    private MetaDriver metaDriver;

    public static final String META_PATH_DELIMETER = "/";
    public static final String META_PATH_HUGEGRAPH = "HUGEGRAPH";
    public static final String META_PATH_GRAPHSPACE = "GRAPHSPACE";
    public static final String META_PATH_SERVICE_CONF = "SERVICE_CONF";
    public static final String META_PATH_GRAPH_CONF = "GRAPH_CONF";

    public MetaHugeClientFactory(String cluster,
                                 MetaDriverType type, String... endpoints) {
        assert cluster != null;
        E.checkArgument(endpoints.length > 0,
                "The length of Meta's Addresses Must > 0");

        this.cluster = cluster;
        if (type == null || type == MetaDriverType.ETCD) {
            this.metaDriver = new EtcdMetaDriver(endpoints);
        } else {
            throw new RuntimeException("Only ETCD SUPPORT");
        }
    }

    public static MetaHugeClientFactory connect(String cluster,
                                                MetaDriverType type,
                                                String... args) {
        MetaHugeClientFactory factory = new MetaHugeClientFactory(cluster,
                                                                  type, args);
        return factory;
    }

    protected String graphKey(String graphSpace, String graph) {
        // HUGEGRAPH/{cluster}/GRAPHSPACE/{graphspace}/GRAPH_CONF/{graph}
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH,
                           this.cluster, META_PATH_GRAPHSPACE, graphSpace,
                           META_PATH_GRAPH_CONF, graph);
    }

    protected String graphServiceKey(String graphSpace, String service) {
        // HUGEGRAPH/{cluster}/GRAPHSPACE/{graphspace}/SERVICE_CONF/{service}
        return String.join(META_PATH_DELIMETER, META_PATH_HUGEGRAPH,
                           this.cluster, META_PATH_GRAPHSPACE, graphSpace,
                           META_PATH_SERVICE_CONF, service);
    }

    public Map<String, Object> getGraphConfig(String graphspace,
                                              String graph) {
        String value = this.metaDriver.get(this.graphKey(graphspace, graph));
        if (value != null) {
            return JsonUtil.fromJson(value, Map.class);
        }
        return null;
    }

    public Map<String, Object> getServiceConfig(String graphspace,
                                              String service) {
        String value = this.metaDriver.get(this.graphServiceKey(graphspace, service));
        if (value != null) {
            return JsonUtil.fromJson(value, Map.class);
        }
        return null;
    }

    @Override
    HugeClient createClient(String graphSpace, String graph, String token) {
        Map<String, Object> graphConfig = this.getGraphConfig(graphSpace, graph);
        if (graphConfig == null) {
            LOG.warn("{}/{} 's graphConfig is null", graphSpace, graph);
            return null;
        }

        LOG.debug("Get {}/{} 's graphConfig From Meta: {}",
                  graphSpace, graph, graphConfig);
        String serviceName = (String) graphConfig.get("service");
        if (serviceName == null) {
            LOG.warn("{}/{} 's servicename is null", graphSpace, graph);
            return null;
        }

        Map<String, Object> graphService = this.getServiceConfig(graphSpace,
                                                                 serviceName);
        if (graphService == null) {
            LOG.warn("{}/{} 's service is null use servicename {}", graphSpace,
                     graph, serviceName);
            return null;
        }

        LOG.debug("Get {}/{} 's serviceConfig From Meta: {}",
                  graphSpace, graph, graphService);

        List<String> urls = (List<String>) graphService.get("urls");
        if (urls == null || urls.size() == 0) {
            LOG.warn("{}/{} 's urls is empty", graphSpace, graph);
            return null;
        }

        int r = (int) Math.floor(Math.random() * urls.size());
        LOG.info("Host of {}/{}: {}, connect {}", graphSpace, graph, urls, r);
        HugeClient client = HugeClient.builder(urls.get(r), graphSpace, graph)
                          .configToken(token).build();

        return client;
    }

    public enum MetaDriverType {
        ETCD,
        PD
    }

    protected interface MetaDriver {
        public String get(String key);
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
    }

    protected class PdMetaDriver implements MetaDriver{
        @Override
        public String get(String key) {
            return null;
        }
    }
}
