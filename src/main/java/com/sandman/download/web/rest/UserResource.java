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
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
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
    public BaseDto createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);
        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserDTO result = userService.save(userDTO);
        return new BaseDto(200,"注册成功!",result);
    }

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
        log.info("this userName is {}", SecurityUtils.getCurrentUserLogin().get());
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
