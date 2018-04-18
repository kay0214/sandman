package com.sandman.download.service;

import com.sandman.download.domain.BaseDto;
import com.sandman.download.domain.Resource;
import com.sandman.download.domain.User;
import com.sandman.download.repository.ResourceRepository;
import com.sandman.download.security.SecurityUtils;
import com.sandman.download.service.dto.ResourceDTO;
import com.sandman.download.service.mapper.ResourceMapper;
import com.sandman.download.web.rest.util.DateUtils;
import com.sandman.download.web.rest.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Resource.
 */
@Service
@Transactional
public class ResourceService {

    private final Logger log = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository resourceRepository;

    private final ResourceMapper resourceMapper;

    @Value(value = "${fileServer.prefix}")
    private String prefix;
    @Autowired
    private UserService userService;

    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper) {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
    }

    /**
     * Save a resource.
     *
     * @param resourceDTO the entity to save
     * @return the persisted entity
     */
    public ResourceDTO save(ResourceDTO resourceDTO) {
        log.debug("Request to save Resource : {}", resourceDTO);
        Resource resource = resourceMapper.toEntity(resourceDTO);
        resource = resourceRepository.save(resource);
        return resourceMapper.toDto(resource);
    }

    /**
     * Get all the resources.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ResourceDTO> findAll() {
        log.debug("Request to get all Resources");
        return resourceRepository.findAll().stream()
            .map(resourceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one resource by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResourceDTO findOne(Long id) {
        log.debug("Request to get Resource : {}", id);
        Resource resource = resourceRepository.findOne(id);
        return resourceMapper.toDto(resource);
    }

    /**
     * Delete the resource by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Resource : {}", id);
        resourceRepository.delete(id);
    }
    /**
     * upload resource
     * */
    public ResourceDTO uploadRes(ResourceDTO resourceDTO, MultipartFile file){
        User user = userService.findUserByUserName(SecurityUtils.getCurrentUserLogin().get());//根据userName查询user信息

        resourceDTO.setUserId(user.getId());//设置UserId给resource

        String fileType = FileUtils.getSuffixNameByFileName(file.getOriginalFilename());
        fileType = (fileType==null || "".equals(fileType))?"file":fileType;//如果utils给出的文件类型为null，将file赋值给fileType
        String filePath = prefix + File.separator + fileType + File.separator + user.getId() + File.separator;//  /var/www/html/spkIMG + / + rar + / + userId + /

        String fileName = resourceDTO.getResName();
        resourceDTO.setResName((fileName==null || "".equals(fileName))?file.getOriginalFilename():fileName);//如果用户设置的resName为空，将原文件名赋值给resName

        log.info("fileName={}",resourceDTO.getResName());
        //resourceDTO.setResGold(0);//用户填写的 下载资源所需积分
        //resourceDTO.setResDesc("");//用户填写的资源描述
        resourceDTO.setResSize(FileUtils.getFileSize(file.getSize()));//获取文件大小，四舍五入保留两位小数

        resourceDTO.setDownCount(0);//设置默认下载数为0，因为是刚上传
        resourceDTO.setCreateTime(DateUtils.getLongTime());//设置创建时间为当前时间
        resourceDTO.setUpdateTime(DateUtils.getLongTime());//设置更新时间为当前时间，因为刚上传
        resourceDTO.setResUrl(filePath + fileName);//设置文件url为 服务器路径+文件类型+userId+fileName
        log.info("url={}",resourceDTO.getResUrl());
        resourceDTO.setResType(1);
        resourceDTO.setStatus(1);
        return resourceDTO;
    }
}
