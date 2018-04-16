package com.sandman.download.service;

import com.sandman.download.domain.BaseDto;
import com.sandman.download.domain.Resource;
import com.sandman.download.repository.ResourceRepository;
import com.sandman.download.service.dto.ResourceDTO;
import com.sandman.download.service.mapper.ResourceMapper;
import com.sandman.download.web.rest.util.DateUtils;
import com.sandman.download.web.rest.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
        String fileName = resourceDTO.getResName();
        resourceDTO.setResName((fileName==null || "".equals(fileName))?file.getOriginalFilename():fileName);
        log.info("fileName={}",resourceDTO.getResName());
        resourceDTO.setDownCount(0);
        resourceDTO.setCreateTime(DateUtils.getLongTime());
        resourceDTO.setUpdateTime(DateUtils.getLongTime());
        resourceDTO.setResSize(FileUtils.getFileSize(file.getSize()));//获取文件大小，四舍五入保留两位小数
        resourceDTO.setResUrl(prefix + "rar/1/" + resourceDTO.getResName());
        log.info("url={}",resourceDTO.getResUrl());
        resourceDTO.setResType(1);
        resourceDTO.setStatus(1);
        return resourceDTO;
    }
}
