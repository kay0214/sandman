package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.domain.BaseDto;
import com.sandman.download.security.SecurityUtils;
import com.sandman.download.service.ResourceService;
import com.sandman.download.web.rest.util.HeaderUtil;
import com.sandman.download.service.dto.ResourceDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing Resource.
 */
@RestController
@RequestMapping("/api/sandman/v1/resource")
public class ResourceResource {

    private final Logger log = LoggerFactory.getLogger(ResourceResource.class);

    private static final String ENTITY_NAME = "resource";

    private final ResourceService resourceService;

    public ResourceResource(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
     * post : upload file
     * */
    @PostMapping("/uploadResource")
    @Timed
    public BaseDto uploadResource(ResourceDTO resourceDTO,@RequestParam("file")MultipartFile file) throws IOException {
        log.info("用户上传资源:{}" + file.getOriginalFilename());
        return resourceService.uploadRes(resourceDTO,file);
    }
    /**
     * get : download file
     * */
    @GetMapping("/downloadResource")
    @Timed
    public BaseDto downloadResource(Long id,HttpServletResponse response){
        log.info("用户下载资源id:{}",id);
        BaseDto baseDto = null;

        try {
            baseDto = resourceService.downloadRes(id, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseDto;
    }

    /**
     * post Updates an existing resource.
     */
    @PostMapping("/updateResource")
    @Timed
    public BaseDto updateResource(ResourceDTO resourceDTO){
        log.info("resourceDto:{}",resourceDTO.toString());
        return resourceService.updateResource(resourceDTO);
    }

    /**
     * GET : get all my resources page.
     */
    @GetMapping("/getAllMyResources")
    @Timed
    public BaseDto getAllMyResources(Integer pageNumber,Integer size,Long userId,String sortType,String order) {
        log.info("pageNumber：{}，size：{}，userId：{}，sortType：{}，order：{}",pageNumber, size, userId, sortType, order);
        Map data = resourceService.getAllMyResources(pageNumber, size, userId, sortType, order);
        return new BaseDto(200,"查询成功!",data);
    }

    /**
     * GET : get one resource
     */
    @GetMapping("/getOneResource")
    @Timed
    public BaseDto getOneResource(Long id) {
        log.debug("REST request to get one Resource : {}", id);
        ResourceDTO resourceDTO = resourceService.getOneResource(id);
        if(resourceDTO!=null)
            return new BaseDto(200,"查询成功!",resourceDTO);
        return new BaseDto(408,"资源不存在!",resourceDTO);
    }

    /**
     * DELETE : delete the "id" resource.
     */
    @GetMapping("/delResource")
    @Timed
    public BaseDto delResource(Long id) {
        log.debug("REST request to delete Resource : {}", id);
        return resourceService.delResource(id);
    }
}
