package com.tany.demo.elasticSearch;

import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.script.Script;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ElasticSerchService {
    private TransportClient client;
    private final String clusterName="my-application";
    private final String transportAddress="192.168.0.108";//填写正确的es服务器ip地址
    private final String transportPort="9300";//默认为9300
    private String indexName;//索引名称
    private String typeName;//type名称


    public ElasticSerchService(String indexName, String typeName){
        this.indexName = indexName;
        this.typeName = typeName;
    }

    public void getESClient(){

        Settings settings = Settings.builder().put("cluster.name", clusterName).build();
        // 创建client
        try {
            client = new PreBuiltTransportClient(settings);
            String[] addrs = transportAddress.split(",");
            for (String addr : addrs) {
                client.addTransportAddress(new TransportAddress(InetAddress.getByName(addr), Integer.parseInt(transportPort)));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看集群信息
     */
    public void getESInfo(){
        List<DiscoveryNode> nodes = client.connectedNodes();
        for (DiscoveryNode node : nodes) {
            System.out.println(node.getHostAddress());
        }
    }

    /**
     * 存入索引中
     * @throws Exception
     */
    public void saveInfoToIndex(Map<String, String > data) throws Exception {
        for(Map.Entry dataItem : data.entrySet()){
            XContentBuilder source = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("user", "kimchy2")
                    .field("postDate", new Date())
                    .field("message", "trying to out ElasticSearch2")
                    .endObject();
            // 存json入索引中
            IndexResponse response = client.prepareIndex(indexName, typeName, "1").setSource(source).get();
//            String index = response.getIndex();
//            String type = response.getType();
//            String id = response.getId();
//            long version = response.getVersion();
//            boolean created = response.isFragment();
//            System.out.println(index + " : " + type + ": " + id + ": " + version + ": " + created);
        }
    }

    /**
     * get API 获取指定文档信息
     */
    public void getTypeInfo() {
        GetResponse response = client.prepareGet(indexName, typeName, "1").get();
        System.out.println(response.getSourceAsString());
    }

    /**
     * 测试 delete api
     */
    public void deleteType() {
        DeleteResponse response = client.prepareDelete(indexName, typeName, "4")
                .get();
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        System.out.println(index + " : " + type + ": " + id + ": " + version);
    }

    /**
     * 测试更新 update API
     * 使用 updateRequest 对象
     * @throws Exception
     */
    public void updateDocInfo() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(indexName);
        updateRequest.type(typeName);
        //如果没有该条doc会报错
        updateRequest.id("1");
        updateRequest.doc(XContentFactory.jsonBuilder()
                .startObject()
                // 对没有的字段添加, 对已有的字段替换
                .field("gender", "male")
                .field("message", "hello")
                .endObject());
        UpdateResponse response = client.update(updateRequest).get();

        // 打印
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        long version = response.getVersion();
        System.out.println(index + " : " + type + ": " + id + ": " + version);
    }

    /**
     * 测试更新 update API by client
     * 使用 updateRequest 对象
     * @throws Exception
     */
    public void updateDocInfoByClient() throws Exception {
        UpdateResponse response = client.update(new UpdateRequest(indexName, typeName, "1")
                .doc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("gender", "male")
                        .endObject()
                )).get();
        System.out.println(response.getIndex());
    }

    /**
     * 测试更新 update API by client 使用脚本
     * 使用 updateRequest 对象
     * @throws Exception,InterruptedException
     */
    public void updateScript() throws InterruptedException, Exception {
        UpdateRequest updateRequest = new UpdateRequest(indexName, typeName, "1")

                .script(new Script("ctx._source.gender=\"male1\""));
        UpdateResponse response = client.update(updateRequest).get();
    }

    /**
     * 测试upsert方法
     * @throws Exception
     *
     */
    public void testUpsert() throws Exception {
        // 设置查询条件, 查找不到则添加生效
        IndexRequest indexRequest = new IndexRequest(indexName, typeName,"4")
                .source(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user", "214")
                        .field("gender", "gfrerq4")
                        .endObject());
        // 设置更新, 查找到更新下面的设置
        UpdateRequest upsert = new UpdateRequest(indexName, typeName, "4")
                .doc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user", "wenbronk4")
                        .endObject())
                .upsert(indexRequest);

        client.update(upsert).get();
    }

    /**
     * 测试multi get api
     * 从不同的index, type, 和id中获取
     */
    public void testMultiGet() {
        MultiGetResponse multiGetResponse = client.prepareMultiGet()
                .add(indexName, typeName, "1")
                .add(indexName, typeName, "2", "3", "4")
                .add("anothoer", "type", "foo")
                .get();

        for (MultiGetItemResponse itemResponse : multiGetResponse) {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()) {
                String sourceAsString = response.getSourceAsString();
                System.out.println(sourceAsString);
            }
        }
    }

    /**
     * bulk 批量执行
     * 一次查询可以update 或 delete多个document
     */
    public void testBulk(List<Map<String, Object>> data) throws Exception {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for(Map<String,Object> dataItem : data){
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, typeName, (String) dataItem.get("id"));
            XContentBuilder  xContentBuilder = XContentFactory.jsonBuilder().startObject();
            for(Map.Entry<String, Object> setItem : dataItem.entrySet()){
                xContentBuilder.field(setItem.getKey(), setItem.getValue());
            }
            xContentBuilder.endObject();
            indexRequestBuilder.setSource(xContentBuilder);
            bulkRequest.add(indexRequestBuilder);
        }
//        bulkRequest.add(client.prepareIndex(indexName, typeName, "1")
//                .setSource(XContentFactory.jsonBuilder()
//                        .startObject()
//                        .field("user", "kimchy")
//                        .field("postDate", new Date())
//                        .field("message", "trying out Elasticsearch")
//                        .endObject()));
//        bulkRequest.add(client.prepareIndex(indexName, typeName, "2")
//                .setSource(XContentFactory.jsonBuilder()
//                        .startObject()
//                        .field("user", "kimchy")
//                        .field("postDate", new Date())
//                        .field("message", "another post")
//                        .endObject()));
        BulkResponse response = bulkRequest.get();
        System.out.println(response.getIngestTookInMillis());
    }

    /**
     * 使用bulk processor
     * @throws Exception
     */
    public void testBulkProcessor() throws Exception {
        // 创建BulkPorcessor对象
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            public void beforeBulk(long paramLong, BulkRequest paramBulkRequest) {
                // TODO Auto-generated method stub
            }

            // 执行出错时执行
            public void afterBulk(long paramLong, BulkRequest paramBulkRequest, Throwable paramThrowable) {
                // TODO Auto-generated method stub
            }

            public void afterBulk(long paramLong, BulkRequest paramBulkRequest, BulkResponse paramBulkResponse) {
                // TODO Auto-generated method stub
            }
        })
                // 1w次请求执行一次bulk
                .setBulkActions(10000)
                // 1gb的数据刷新一次bulk
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
                // 固定5s必须刷新一次
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                // 并发请求数量, 0不并发, 1并发允许执行
                .setConcurrentRequests(1)
                // 设置退避, 100ms后执行, 最大请求3次
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        // 添加单次请求
        bulkProcessor.add(new IndexRequest(indexName, typeName, "1"));
        bulkProcessor.add(new DeleteRequest(indexName, typeName, "2"));

        // 关闭
        bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        // 或者
        bulkProcessor.close();
    }

}
