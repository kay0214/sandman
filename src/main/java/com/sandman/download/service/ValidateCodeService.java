package com.sandman.download.service;

import com.sandman.download.domain.BaseDto;
import com.sandman.download.domain.ValidateCode;
import com.sandman.download.repository.ValidateCodeRepository;
import com.sandman.download.service.dto.ValidateCodeDTO;
import com.sandman.download.service.mapper.ValidateCodeMapper;
import com.sandman.download.web.rest.util.DateUtils;
import com.sandman.download.web.rest.util.RandomUtils;
import com.sandman.download.web.rest.util.TemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing ValidateCode.
 */
@Service
@Transactional
public class ValidateCodeService {

    private final Logger log = LoggerFactory.getLogger(ValidateCodeService.class);

    private final ValidateCodeRepository validateCodeRepository;

    private final ValidateCodeMapper validateCodeMapper;

    @Autowired
    private SendEmailService sendEmailService;

    public ValidateCodeService(ValidateCodeRepository validateCodeRepository, ValidateCodeMapper validateCodeMapper) {
        this.validateCodeRepository = validateCodeRepository;
        this.validateCodeMapper = validateCodeMapper;
    }

    /**
     * Save a validateCode.
     */
    public BaseDto sendValidateCode(ValidateCodeDTO validateCodeDTO) {
        log.debug("Request to send ValidateCode : {}", validateCodeDTO);
        if(validateCodeDTO.getContact()==null || "".equals(validateCodeDTO.getContact()))
            return new BaseDto(413,"请先填写联系方式!");
        //如果库里有这个联系方式的数据，就删除
        deleteByContact(validateCodeDTO.getContact());
        validateCodeDTO.setCode(RandomUtils.getValidateCode());//获取6位数随机码
        validateCodeDTO.setCreateTime(DateUtils.getLongTime());//创建时间
        validateCodeDTO.setDeadLine(DateUtils.getMinLater(5));//有效时间5分钟
        validateCodeDTO.setIsValid(0);
        validateCodeDTO.setSended(0);
        ValidateCode validateCode = validateCodeMapper.toEntity(validateCodeDTO);
        validateCode = validateCodeRepository.save(validateCode);//保存到数据库
        boolean sendSuccess = false;
        if(validateCodeDTO.getContact().contains("@")){//如果包含@，则发送邮件验证码
            String emailContent = TemplateUtils.getTemplateByName("emailCode");
            emailContent = emailContent.replaceAll("\\$\\(recipient\\)",validateCodeDTO.getContact());//替换邮箱收件人
            emailContent = emailContent.replaceAll("\\$\\(emailCode\\)",validateCodeDTO.getCode());//替换验证码
            sendSuccess = sendEmailService.sendEmail("注册验证码",emailContent,validateCodeDTO.getContact());
            log.info("邮箱:[{}]已发送验证码[{}]",validateCodeDTO.getContact(),validateCodeDTO.getCode());
        }else{//发送手机验证码
            log.info("手机号:[{}]已发送验证码[{}]",validateCodeDTO.getContact(),validateCodeDTO.getCode());
            sendSuccess = true;//假如手机验证码发送成功
        }
        if(sendSuccess){
            validateCode.setIsValid(1);
            validateCode.setSended(1);
            return new BaseDto(200,"验证码发送成功");
        }


        return new BaseDto(414,"发送验证码失败");
    }

    public ValidateCode findByContact(String contact){
        return validateCodeRepository.findByContact(contact);
    }

    /**
     * Delete the validateCode by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete ValidateCode : {}", id);
        validateCodeRepository.delete(id);
    }
    /**
     * delete by contact
     * */
    public void deleteByContact(String contact){
        validateCodeRepository.deleteByContact(contact);
    }
}
