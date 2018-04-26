package com.sandman.download.web.rest;

import com.sandman.download.config.SingleElasticSearchClientConfig;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangj on 2018/4/12.
 */
@RestController
@RequestMapping("/api/sandman/v1/user")
public class UploadResource {
    @GetMapping("/test")
    public String test(){
        TransportClient client = SingleElasticSearchClientConfig.getInstance().getClient();
        Long startTime = System.currentTimeMillis();
        SearchResponse response = client.prepareSearch().setIndices("ik_test").setTypes("person")
            .addSort("_id", SortOrder.DESC)
            .get();
        SearchHits searchHits = response.getHits();
        SearchHit hit = searchHits.getAt(0);
        System.out.println("(排序 + 取数)耗时：" + (System.currentTimeMillis() - startTime));
        return hit.getId();
    }
}
