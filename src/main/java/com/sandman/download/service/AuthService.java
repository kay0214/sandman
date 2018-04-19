package com.sandman.download.service;

import com.sandman.download.domain.User;
import com.sandman.download.repository.UserRepository;
import com.sandman.download.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangj on 2018/4/12.
 */
@Service
public class AuthService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        System.out.println("userName=" + userName);
        User user = userRepository.findByUserName(userName);
        if(user==null)
            throw new UsernameNotFoundException("user not found!");

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        CurrentUser userAndAuth = new CurrentUser(user.getUserName(),user.getPassword(),authorityList);
        userAndAuth.setUserName(user.getUserName());
        userAndAuth.setId(user.getId());
        userAndAuth.setMobile(user.getMobile());
        userAndAuth.setEmail(user.getEmail());
        userAndAuth.setGold(user.getGold());
        return userAndAuth;
    }
    //用户权限
/*    public void getRoles(User user,List<GrantedAuthority> authorityList){

    }*/
}
