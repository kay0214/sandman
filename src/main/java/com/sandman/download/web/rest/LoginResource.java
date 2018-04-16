package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wangj on 2018/4/13.
 */
@RestController
@RequestMapping("/api/sandman/v1/LoginResource")
public class LoginResource {
    private final Logger log = LoggerFactory.getLogger(LoginResource.class);

    @PostMapping("/login")
    @Timed
    public void login(){
        log.info("this is login page!");
    }
    @GetMapping("/error")
    @Timed
    public void error(){
        log.info("login error:userName or password error!");
    }
    @GetMapping("/success")
    @Timed
    public void success(){
        log.info("this userName is {}",SecurityUtils.getCurrentUserLogin().get());
        log.info("login success!");
    }
    @GetMapping("/logout")
    @Timed
    public void logout(){
        log.info("this is logout page!");
    }
    @GetMapping("/logoutSuccess")
    @Timed
    public void logoutSuccess(){
        log.info("logout success!");
    }
}
