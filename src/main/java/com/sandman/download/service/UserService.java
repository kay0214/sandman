package com.sandman.download.service;

import com.sandman.download.domain.User;
import com.sandman.download.domain.ValidateCode;
import com.sandman.download.repository.UserRepository;
import com.sandman.download.service.dto.UserDTO;
import com.sandman.download.service.mapper.UserMapper;
import com.sandman.download.web.rest.util.DateUtils;
import com.sandman.download.web.rest.util.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
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
     *
     * @param userDTO the entity to save
     * @return the persisted entity
     */
    public UserDTO createUser(UserDTO userDTO) {
        log.debug("Request to save User : {}", userDTO);
        User existUser = userRepository.findByUserName(userDTO.getUserName());
        if(existUser!=null)
            return null;
        userDTO.setPassword(PasswordUtils.getSecretPasswordSpring(userDTO.getPassword()));//密码加密
        userDTO.setGold(0);
        userDTO.setCreateTime(DateUtils.getLongTime());
        userDTO.setUpdateTime(DateUtils.getLongTime());
        userDTO.setAvailable(1);
        User user = userMapper.toEntity(userDTO);
        user = userRepository.save(user);
        validateCodeService.deleteByContact(userDTO.getEmail());//如果是使用email注册成功，按照email删除验证码
        validateCodeService.deleteByContact(userDTO.getMobile());//如果是使用手机号注册成功，按照手机号删除验证码
        return userMapper.toDto(user);
    }
    public UserDTO updateUser(User user){
        user.setUpdateTime(DateUtils.getLongTime());
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * 验证码校验
     * */
    public boolean verifyCode(UserDTO userDTO){//直接按照邮箱找，省了发手机短信的钱。后期要改造这里
        ValidateCode validateCode = validateCodeService.findByContact(userDTO.getEmail());
        if(DateUtils.getLongTime()>validateCode.getDeadLine()){
            return false;
        }
        return userDTO.getValidateCode().equals(validateCode.getCode());
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
