package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.domain.BaseDto;
import com.sandman.download.domain.User;
import com.sandman.download.security.SecurityUtils;
import com.sandman.download.service.UserService;
import com.sandman.download.web.rest.errors.BadRequestAlertException;
import com.sandman.download.web.rest.util.HeaderUtil;
import com.sandman.download.service.dto.UserDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing User.
 */
@RestController
@RequestMapping("/api/sandman/v1/user")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "user";

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST : Create a new user.
     */
    @PostMapping("/createUser")
    @Timed
    public BaseDto createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {//这里进行简单校验，在service里面进行复杂校验
        log.debug("REST request to save User : {}", userDTO);
        if (userDTO.getId() != null) {
            return new BaseDto(415,"创建用户你带什么ID啊!");
        }
        if(userDTO.getValidateCode()==null || "".equals(userDTO.getValidateCode())){
            return new BaseDto(416,"请先填写验证码!");
        }

        return userService.createUser(userDTO);
    }
    @GetMapping("/contactExist")
    @Timed
    public Map<String, Integer> contactExist(String contact){
        Map<String,Integer> map = new HashMap<>();
        if(contact==null || "".equals(contact) || "null".equals(contact)){
            map.put("exist",0);
            return map;
        }
        map.put("exist",userService.contactExist(contact));//0:未传入联系方式；1:联系方式已经被绑定；2:联系方式未被绑定
        return map;
    }

    @PostMapping("/login")
    @Timed
    public void login(){
        //用户登录进来所需要做的操作
        log.info("this is login page!");
    }
    @PostMapping("/error")
    @Timed
    public BaseDto error(){
        log.info("login error:userName or password error!");
        return new BaseDto(411,"用户名或密码错误!");
    }
    @PostMapping("/success")
    @Timed
    public BaseDto success(){
        log.info("this userName is {}", SecurityUtils.getCurrentUserLogin().get());
        log.info("login success!");
        return new BaseDto(200,"用户登录成功!");
    }
    @GetMapping("/logout")
    @Timed
    public void logout(){
        log.info("this is logout page!");
    }
    @GetMapping("/logoutSuccess")
    @Timed
    public BaseDto logoutSuccess(){
        log.info("logout success!");
        return new BaseDto(200,"用户退出成功！");
    }
}
