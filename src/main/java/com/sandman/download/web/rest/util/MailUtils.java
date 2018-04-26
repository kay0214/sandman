package com.sandman.download.web.rest.util;

import org.junit.jupiter.api.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by wangj on 2018/4/26.
 */
public class MailUtils {
    public static final String SMTPSERVER = "smtp.yeah.net";
    public static final String SMTPPORT = "25";
    public static final String ACCOUT = "sunpeikai@yeah.net";
    public static final String PWD = "spkzq521";

    public static void testSendEmail() throws Exception {
        try {
            // 创建Properties 类用于记录邮箱的一些属性
            Properties props = new Properties();
            // 表示SMTP发送邮件，必须进行身份验证
            props.put("mail.smtp.auth", "true");
            //此处填写SMTP服务器
            props.put("mail.smtp.host", SMTPSERVER);
            //端口号，QQ邮箱给出了两个端口，但是另一个我一直使用不了，所以就给出这一个587
            props.put("mail.smtp.port", SMTPPORT);
            // 此处填写你的账号
            props.put("mail.user", ACCOUT);
            // 此处的密码就是前面说的16位STMP口令
            props.put("mail.password", PWD);

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    return new PasswordAuthentication(ACCOUT, PWD);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            message.setFrom(new InternetAddress(ACCOUT));

            // 设置收件人的邮箱
            InternetAddress to = new InternetAddress("929525390@qq.com");
            message.setRecipient(Message.RecipientType.TO, to);

            //设置多个发件人
            Address[] addresses = new Address[]{
                new InternetAddress(ACCOUT)};
//                new InternetAddress("571269164@qq.com")};
            message.addRecipients(Message.RecipientType.TO,addresses);

            // 设置邮件标题
            message.setSubject("LVMH官微 ： LVMH正式服，报错内容如下：");

            // 设置邮件的内容体
            message.setContent( "hellome" , "text/html;charset=UTF-8");

            // 最后当然就是发送邮件啦
            Transport.send(message);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static MimeMessage createEmail(Session session) throws Exception {
        // 根据会话创建邮件
        MimeMessage msg = new MimeMessage(session);
        // address邮件地址, personal邮件昵称, charset编码方式
        InternetAddress fromAddress = new InternetAddress(ACCOUT,
            "kimi", "utf-8");
        // 设置发送邮件方
        msg.setFrom(fromAddress);
        InternetAddress receiveAddress = new InternetAddress(
            "929525390@qq.com", "test", "utf-8");
        // 设置邮件接收方
        msg.setRecipient(MimeMessage.RecipientType.TO, receiveAddress);
        // 设置邮件标题
        msg.setSubject("测试标题", "utf-8");
        msg.setText("我是个程序员，一天我坐在路边一边喝水一边苦苦检查程序。 这时一个乞丐在我边上坐下了，开始要饭，我觉得可怜，就给了他1块钱。 然后接着调试程序。他可能生意不好，就无聊的看看我在干什么，然后过了一会，他缓缓地指着我的屏幕说，这里少了个分号");
        // 设置显示的发件时间
        msg.setSentDate(new Date());
        // 保存设置
        msg.saveChanges();
        return msg;
    }
    public static void main(String[] args) throws Exception {
        testSendEmail();
    }
}
