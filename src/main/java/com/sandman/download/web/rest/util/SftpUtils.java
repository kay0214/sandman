package com.sandman.download.web.rest.util;

import com.jcraft.jsch.*;
import com.sandman.download.config.JschUserInfoImpl;
import com.sandman.download.domain.SftpParam;

/**
 * Created by wangj on 2018/4/17.
 */
public class SftpUtils {
    /**
     * 获取sftp连接
     */
    public static ChannelSftp getSftp() {
        ChannelSftp sftp = null;
        try {
            JSch jSch = new JSch();
/*
            Session session = jSch.getSession(SftpParam.getUSERNAME(),SftpParam.getHOST(),SftpParam.getSshPort());
            session.setPassword(SftpParam.getPASSWORD());
*/

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
        return sftp;
    }
    /**
     * 关闭sftp连接
     * */
    public static void closeSftpCon(ChannelSftp sftp) {
        try {
            sftp.exit();
            sftp.getSession().disconnect();
        } catch (JSchException e) {
            System.out.println("close sftp failed");
        }
    }
/*    public static void main(String[] args){
        FileUtils.mkDirectory("/var/www/html/spkIMG/123/456/789");

    }*/
}
