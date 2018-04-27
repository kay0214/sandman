package com.sandman.download.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

/**
 * Created by wangj on 2018/4/27.
 */
@Service
public class SendEmailService {
    @Value("${mail.smtp.host}")
    private String SMTPHOST;
    @Value("${mail.smtp.port}")
    private String SMTPPORT;
    @Value("${mail.senderAccount}")
    private String SENDERACCOUNT;
    @Value("${mail.password}")
    private String PASSWORD;
    @Value("${mail.myNickName}")
    private String MYNICKNAME;
    /**
     * 发邮件
     * @Param content:邮件正文内容
     * @Param subject:邮件标题
     * @Param recipient:收件人地址，多个地址用 "," 隔开
     * */
    public boolean sendEmail(String subject,String content,String recipients){
        Long startTime = System.currentTimeMillis();
        try {
            // 创建Properties 类用于记录邮箱的一些属性
            Properties props = new Properties();
            //设置传输协议
            props.setProperty("mail.transport.protocol","smtp");
            // 表示SMTP发送邮件，必须进行身份验证
            props.setProperty("mail.smtp.auth", "true");
            //此处填写SMTP服务器
            props.setProperty("mail.smtp.host", SMTPHOST);
            //端口号，网易相关的邮箱都是25端口,QQ邮箱587端口
            props.setProperty("mail.smtp.port", SMTPPORT);

            Session session = Session.getInstance(props);

            //开启debug模式，console会打印日志
            //session.setDebug(true);

            Message message = getMessageWithTemplate(session,subject,content,recipients);



            Transport transport = session.getTransport();

            transport.connect(SENDERACCOUNT,PASSWORD);

            transport.sendMessage(message,message.getAllRecipients());

            transport.close();

            System.out.println("发送邮件耗时:" + (System.currentTimeMillis() - startTime));

            return true;
        }catch (Exception e) {
            return false;
        }
    }
    /**
     * 一封带html格式模板的邮件
     * @Param content:邮件正文内容
     * @Param subject:邮件标题
     * @Param recipient:收件人地址，多个地址用 "," 隔开
     * */
    private MimeMessage getMessageWithTemplate(Session session,String subject,String content,String recipients){
        try{
            // 根据会话创建邮件
            MimeMessage message = new MimeMessage(session);
            // 设置发送邮件方：发件人账号，发件人昵称
            message.setFrom(new InternetAddress(SENDERACCOUNT,MYNICKNAME, "utf-8"));
            // 设置邮件接收方
            message.setRecipients(MimeMessage.RecipientType.TO,parseRecipients(recipients));
            // 设置邮件标题
            message.setSubject(subject,"utf-8");

            message.setContent(content,"text/html;charset=UTF-8");

            // 设置显示的发件时间
            message.setSentDate(new Date());
            // 保存设置
            message.saveChanges();
            return message;
        }catch(Exception e){
            System.out.println("创建邮件失败!");
            return null;
        }
    }
    /**
     * 解析输入的邮箱地址
     * */
    private static Address[] parseRecipients(String recipients)throws AddressException{
        if(recipients.contains(",")){//如果包含多个收件人地址
            String[] recipient = recipients.split(",");
            Address[] addresses = new Address[recipient.length];
            for(int i=0;i<recipient.length;i++){
                addresses[i] = new InternetAddress(recipient[i]);
            }

            return addresses;
        }else{//如果只有一个收件人地址
            Address[] addresses = {new InternetAddress(recipients)};
            return addresses;
        }
    }

}
