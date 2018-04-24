package com.sandman.download.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wangj on 2018/4/24.
 */
@Component
public class ElasticSearchConfiguration {
    private TransportClient client = null;
    public void setClientConfig(){
        try{
            Settings settings = Settings.builder()
                .put("cluster.name","sunpk-es")
                .put("client.transport.sniff", true)
                .build();
            client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("192.168.1.130"),9300));
        }catch(UnknownHostException e){
            client.close();
        }
    }
    public TransportClient getClient(){
        if(client==null){
            synchronized(this.getClass()){
                if(client==null){
                    setClientConfig();
                }
            }

        }
        return client;
    }
}
