package com.sandman.download.web.rest.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wangj on 2018/4/20.
 */
public class PasswordUtils {
    public static String getSecretPasswordSpring(String oriPassword){
        return new BCryptPasswordEncoder().encode(oriPassword);
    }
    public static String getSecretPasswordMD5(String oriPassword){
        StringBuffer sb = new StringBuffer(32);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(oriPassword.getBytes("utf-8"));

            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
            }
        } catch (Exception e) {
            System.out.println("Can not encode the string '" + oriPassword + "' to MD5。 Exception:" + e);
            return null;
        }
        return sb.toString();
    }
}
