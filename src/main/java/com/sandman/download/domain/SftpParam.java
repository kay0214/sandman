package com.sandman.download.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by wangj on 2018/4/17.
 */
@Component
public class SftpParam {
    @Value("${sftpServer.userName}")
    private static String USERNAME;
    @Value("${sftpServer.password}")
    private static String PASSWORD;
    @Value("${sftpServer.host}")
    private static String HOST;
    @Value("${sftpServer.sshPort}")
    private static int SSH_PORT;

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static String getHOST() {
        return HOST;
    }

    public static int getSshPort() {
        return SSH_PORT;
    }
}
