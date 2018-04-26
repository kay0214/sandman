package com.sandman.download.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wangj on 2018/4/25.
 */
public class SingleElasticSearchClientConfig {


    private static String CLUSTER_NAME = "sunpk-es";
    private static String HOST = "192.168.1.130";
    private static Integer PORT = 9303;


    private Settings settings;
    private TransportClient client;

    private SingleElasticSearchClientConfig(){
        try {
            System.out.println("CLUSTER_NAME:" + CLUSTER_NAME + ";HOST:" + HOST + ";PORT:" + PORT);
            settings = Settings.builder()
                .put("cluster.name",CLUSTER_NAME)//集群名
                .put("client.transport.sniff", true)//开启嗅探功能，自动搜索集群内的其他节点
                .build();
            client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(HOST),PORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public TransportClient getClient(){
        return client;
    }

    public static final SingleElasticSearchClientConfig getInstance(){
        return ClientHolder.INSTANCE;
    }
    //静态内部类的单例模式
    private static class ClientHolder{
        private static final SingleElasticSearchClientConfig INSTANCE = new SingleElasticSearchClientConfig();
    }



}
