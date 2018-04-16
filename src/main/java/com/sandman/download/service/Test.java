package com.sandman.download.service;

import com.sandman.download.security.SecurityUtils;

/**
 * Created by wangj on 2018/4/16.
 */
public class Test {
    public static void main(String[] args){
        String userName = SecurityUtils.getCurrentUserLogin().get();
    }
}
