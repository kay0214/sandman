package com.sandman.download.web.rest;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by wangj on 2018/4/24.
 */
public class ElasticSearchTest {
    public static void main(String[] args) throws IOException {
        System.out.println("max id is:" + getMaxId());

/*        TransportClient client = getClient();

        IndexResponse response = client.prepareIndex("ik_test","person","6").setSource(XContentFactory.jsonBuilder()
            .startObject().field("content","我爱你中国，亲爱的母亲!")
            .endObject()).get();
        System.out.println(response.toString());*/
    }
    public static String createIndex(String index,String type){
        return null;
    }
    /**
     * 获取id字段最大值，用于插入下一条记录
     * 思路:将全部记录按照id 倒序排序，取第一条记录的id 值返回
     * (排序 + 取数)首次耗时 0.29秒，其次耗时0.06秒，再次耗时0.03秒
     * */
    public static Integer getMaxId(){
        TransportClient client = getClient();
        Long startTime = System.currentTimeMillis();
        SearchResponse response = client.prepareSearch().setIndices("ik_test").setTypes("person")
            .addSort("_id", SortOrder.DESC)
/*            .setQuery(QueryBuilders.matchAllQuery())
            .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)*/
            .get();
        SearchHits searchHits = response.getHits();
        SearchHit hit = searchHits.getAt(0);
        System.out.println("(排序 + 取数)耗时：" + (System.currentTimeMillis() - startTime));
        closeConnection(client);
        return Integer.parseInt(hit.getId());
    }
    /**
     * 获取到client连接
     * 与elasticSearch建立连接耗时稳定2秒
     * */
    public static TransportClient getClient(){
        Long startTime = System.currentTimeMillis();
        Settings settings = Settings.builder()
                            .put("cluster.name","sunpk-es")
                            .put("client.transport.sniff", true)
                            .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        try {
            client.addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.130"),9303));
            List<DiscoveryNode> discoveryNodeList = client.connectedNodes();
            discoveryNodeList.forEach(discoveryNode -> {
                System.out.println("this node name is:" + discoveryNode.getName());
            });
        } catch (UnknownHostException e) {
            client.close();
        }
        System.out.println("与elasticSearch建立连接耗时:" + (System.currentTimeMillis() - startTime));
        return client;
    }
    /**
     * 关闭连接
     * 稳定耗时1.8秒左右
     * */
    public static void closeConnection(TransportClient client){
        Long startTime = System.currentTimeMillis();
        client.close();
        System.out.println("关闭elasticSearch连接耗时:" + (System.currentTimeMillis() - startTime));
    }
}
