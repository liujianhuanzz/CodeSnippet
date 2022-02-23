package cn.hhspace.flink.sink;

import cn.hhspace.flink.source.SensorReading;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.threadpool.ThreadPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Descriptions:
 * @Date: 2022/2/13 5:22 下午
 * @Package: cn.hhspace.flink.sink
 */
public class FlinkEsOutputProcessFunction extends ProcessFunction<SensorReading, SensorReading> {

    BulkProcessor bulkProcessor;
    String esLists;

    public FlinkEsOutputProcessFunction(String esLists) {
        this.esLists = esLists;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        bulkProcessor = getEsBulkProcessor(getRestClient());
    }

    /*public TransportClient getTransportClient() {
        Settings.Builder settingBuilder = Settings.builder();
        settingBuilder.put("xpack.security.user", "es_admin:%36.Hadoop*");
        settingBuilder.put("client.transport.ignore_cluster_name", "true");
        settingBuilder.put("transport.type", "security3");
        settingBuilder.put("http.type", "security3");
        TransportClient client = new PreBuiltXPackTransportClient(settingBuilder.build());
        try {
            client.addTransportAddress(new InetSocketTransportAddress(
                    InetAddress.getByName("hostname.cn"),
                    9300
            ));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }*/

    public RestClient getRestClient() {
        String[] splitEs = esLists.split(",");
        HttpHost[] HttpEsLists = new HttpHost[splitEs.length];
        for (int i=0; i < splitEs.length; i++) {
            HttpEsLists[i] = new HttpHost(splitEs[i], 9200, "http");
        }

        RestClientBuilder builder = RestClient.builder(HttpEsLists);

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("es_admin", "%36.Hadoop*"));

        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });

        return builder.build();
    }

    @Override
    public void close() throws Exception {
        bulkProcessor.flush();
        bulkProcessor.awaitClose(3, TimeUnit.SECONDS);
    }

    public IndexRequest createIndexRequest(SensorReading sensorReading) throws JsonProcessingException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String suffix = sdf.format(new Date(sensorReading.timestamp));
        return Requests.indexRequest().index(getEsIndex(suffix)).type(getEsType()).source(new ObjectMapper().writeValueAsString(sensorReading));
    }

    public static BulkProcessor getEsBulkProcessor(Object client) {
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId,
                                   BulkRequest request) {
                // do beforeBulk
            }

            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  BulkResponse response) {
                // do afterBulk
                if (response.hasFailures()) {
                    String s = response.buildFailureMessage();
                    System.out.println(s);
                }
            }

            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  Throwable failure) {
                // do afterBulk Throwable
            }
        };

        int bulkSize = 30000;
        int byteSize = 5;
        int flushInterval = 300;
        int concurrentRequests = 10;

        BulkProcessor bulkProcessor = null;

        if (client instanceof TransportClient) {
            bulkProcessor = BulkProcessor.builder(
                            (TransportClient) client,
                            listener
                    ).setBulkActions(bulkSize)
                    .setBulkSize(new ByteSizeValue(byteSize, ByteSizeUnit.MB))
                    .setFlushInterval(TimeValue.timeValueSeconds(flushInterval))
                    .setConcurrentRequests(concurrentRequests)
                    .setBackoffPolicy(BackoffPolicy.noBackoff())
                    .build();
        } else if (client instanceof RestClient) {
            //TODO
            RestHighLevelClient restHighLevelClient = new RestHighLevelClient((RestClient) client);
            bulkProcessor = new BulkProcessor.Builder(
                    restHighLevelClient::bulkAsync,
                    listener,
                    new ThreadPool(Settings.builder().build())
            ).setBulkActions(bulkSize)
                    .setBulkSize(new ByteSizeValue(byteSize, ByteSizeUnit.MB))
                    .setFlushInterval(TimeValue.timeValueSeconds(flushInterval))
                    .setConcurrentRequests(concurrentRequests)
                    .setBackoffPolicy(BackoffPolicy.noBackoff())
                    .build();
        }
        return bulkProcessor;
    }

    private String getEsType() {
        return "flink_test_es";
    }

    private String getEsIndex(String suffix) {

        return String.format("%s_%s", "flink_test_es", suffix);
    }

    @Override
    public void processElement(SensorReading sensorReading, ProcessFunction<SensorReading, SensorReading>.Context context, Collector<SensorReading> collector) throws Exception {
        bulkProcessor.add(createIndexRequest(sensorReading));
    }
}
