package com.sandman.download.web.rest.util;

import com.jcraft.jsch.*;
import com.sandman.download.config.JschUserInfoImpl;
import rx.internal.util.ObjectPool;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangj on 2018/4/26.
 */
public class SftpPool{

    private static final List<ChannelSftp> sftpList = new LinkedList<>();
    private static final int MIN_CONNECTION = 0;
    private static final int MAX_CONNECTION = 5;
    /**
     * 获取sftp连接
     */
    public static ChannelSftp getSftp() {
        if(sftpList.size()>MIN_CONNECTION){
            //System.out.println("直接返回list里的连接");
            //连接池里的连接>0个，直接返回list的第一个连接
            return sftpList.remove(0);//取list的第一个连接返回
        }else{
            //System.out.println("创建连接并返回list里的连接");
            sftpList.add(createSftp());
            return sftpList.remove(0);
        }
    }
    /**
     * 将一个sftp归还连接池
     * */
    public static void returnSftp(ChannelSftp sftp){
        if(sftpList.size()>=MAX_CONNECTION){//如果连接池的连接数大于5个，那就把归还的连接直接销毁
            //System.out.println("连接池达到上限，直接销毁连接");
            closeSftpCon(sftp);
        }else{
            //System.out.println("将连接归还到list");
            sftpList.add(sftp);
        }
    }
    /**
     * 创建一个新的sftp连接
     * */
    private static ChannelSftp createSftp(){
        //Long startTime = System.currentTimeMillis();
        ChannelSftp sftp = null;
        try {
            JSch jSch = new JSch();

            //一台特定机器，后期考虑分布式的话需要改造
/*            Session session = jSch.getSession(SftpParam.getUSERNAME(),SftpParam.getHOST(),SftpParam.getSshPort());
            session.setPassword(SftpParam.getPASSWORD());*/

            Session session = jSch.getSession("root","39.104.80.30",22);
            session.setPassword("abcd54321");

            session.setConfig("StrictHostKeyChecking", "no");
            session.setUserInfo(new JschUserInfoImpl());

            session.connect(30000);
            Channel channel = session.openChannel("sftp");
            channel.connect(3 * 1000);
            sftp = (ChannelSftp) channel;

        } catch (JSchException e) {
            System.out.println(e);
        }
        //System.out.println("创建一个新sftp连接耗时:" + (System.currentTimeMillis() - startTime));
        return sftp;
    }

    /**
     * 关闭sftp连接
     * */
    public static void closeSftpCon(ChannelSftp sftp) {
        //Long startTime = System.currentTimeMillis();
        try {
            sftp.exit();
            sftp.getSession().disconnect();
            sftp = null;
        } catch (JSchException e) {
            System.out.println("close sftp failed");
        }
        //System.out.println("关闭sftp连接耗时:" + (System.currentTimeMillis() - startTime));
    }
    public static void closeAllSftpCon(){
        while(sftpList.size()>0){
            closeSftpCon(sftpList.remove(0));
        }
    }
}
