package com.sandman.download.config;

import com.jcraft.jsch.UserInfo;

/**
 * Created by wangj on 2018/4/17.
 */
public class JschUserInfoImpl implements UserInfo {
    @Override
    public String getPassphrase() {
        System.out.println("this is getPassphrase method");
        return null;
    }

    @Override
    public String getPassword() {
        System.out.println("this is getPassword method");
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        System.out.println("this is promptPassword method");
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        System.out.println("this is promptPassphrase method");
        return false;
    }

    @Override
    public boolean promptYesNo(String s) {
        System.out.println("this is promptYesNo method");
        return false;
    }

    @Override
    public void showMessage(String s) {
        System.out.println("this is showMessage method");
    }
}
