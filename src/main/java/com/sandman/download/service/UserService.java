package com.sandman.download.service;

import com.sandman.download.domain.BaseDto;
import com.sandman.download.domain.User;
import com.sandman.download.domain.ValidateCode;
import com.sandman.download.repository.UserRepository;
import com.sandman.download.security.SecurityUtils;
import com.sandman.download.service.dto.UserDTO;
import com.sandman.download.service.mapper.UserMapper;
import com.sandman.download.web.rest.util.DateUtils;
import com.sandman.download.web.rest.util.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing User.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Autowired
    private ValidateCodeService validateCodeService;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Save a user.
     */
    public BaseDto createUser(UserDTO userDTO) {
        log.debug("Request to save User : {}", userDTO);
        User existUser = userRepository.findByUserName(userDTO.getUserName());
        if(existUser!=null)//用户名校验
            return new BaseDto(409,"用户名已存在!");

        //验证码过期校验
        boolean overdue = overdueCode(userDTO);
        if(overdue){//已经过期return，未过期就继续往下走
            deleteValidateCode(userDTO);//验证码过期，异步删除验证码
            return new BaseDto(418,"未发送验证码或验证码已过期!");
        }

        //验证码正确性校验
        boolean verifySuccess = verifyCode(userDTO);
        if(!verifySuccess)//校验失败return，否则继续往下走
            return new BaseDto(417,"验证码不正确!",userDTO);
        //所有校验已经完成，创建用户

        userDTO.setPassword(PasswordUtils.getSecretPasswordSpring(userDTO.getPassword()));//密码加密
        userDTO.setGold(0);
        userDTO.setCreateTime(DateUtils.getLongTime());
        userDTO.setUpdateTime(DateUtils.getLongTime());
        userDTO.setAvailable(1);
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
        deleteValidateCode(userDTO);//注册完成，异步删除验证码
        return new BaseDto(200,"注册成功!",userMapper.toDto(user));

    }
    public UserDTO getCurUserInfo(){
        User currentUser = userRepository.findOne(SecurityUtils.getCurrentUserId());//根据userId查询user信息
        UserDTO user = userMapper.toDto(currentUser);
        user.setPassword(null);
        user.setMobile(null);
        user.setEmail(null);
        return user;
    }
    public UserDTO updateUser(User user){
        user.setUpdateTime(DateUtils.getLongTime());
        return userMapper.toDto(userRepository.save(user));
    }
    /**
     * 验证联系方式是否已经被绑定，这里判定是何种联系方式的方法是正则表达式
     * */
    public Integer contactExist(String contact){
        List<User> userList = new ArrayList<>();
        if(contact.contains("@")){//验证邮箱是否已经被绑定
            userList = userRepository.findByEmail(contact);
        }else{//验证手机号是否已经被绑定
            userList = userRepository.findByMobile(contact);
        }

        if(userList.size()>0)//userList>0，表示已经存在，返回1
            return 1;
        return 2;
    }
    /**
     * 验证码过期校验
     * */
    public boolean overdueCode(UserDTO userDTO){//直接按照邮箱找，省了发手机短信的钱。后期要改造这里
        ValidateCode validateCode = validateCodeService.findByContact(userDTO.getEmail());
        if(null==validateCode || DateUtils.getLongTime()>validateCode.getDeadLine()){
            return true;
        }
        return false;
    }
    /**
     * 验证码正确性校验
     * */
    public boolean verifyCode(UserDTO userDTO){//直接按照邮箱找，省了发手机短信的钱。后期要改造这里
        ValidateCode validateCode = validateCodeService.findByContact(userDTO.getEmail());
        if(null==validateCode || DateUtils.getLongTime()>validateCode.getDeadLine()){
            return false;
        }
        return userDTO.getValidateCode().equals(validateCode.getCode());
    }
    /**
     * 删除已经完成注册的验证码和校验失败的验证码.异步删除
     * */
    @Async
    public void deleteValidateCode(UserDTO userDTO){
        validateCodeService.deleteByContact(userDTO.getEmail());//如果是使用email注册成功，按照email删除验证码
        validateCodeService.deleteByContact(userDTO.getMobile());//如果是使用手机号注册成功，按照手机号删除验证码
    }
    /**
     * Get all the users.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        log.debug("Request to get all Users");
        return userRepository.findAll().stream()
            .map(userMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one user by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public User findOne(Long id) {
        log.debug("Request to get User : {}", id);
        return userRepository.findOne(id);
    }

    /**
     * Delete the user by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete User : {}", id);
        userRepository.delete(id);
    }
    /**
     * 根据userName获取User信息
     * */
    public User findUserByUserName(String userName){
        return userRepository.findByUserName(userName);
    }
}
