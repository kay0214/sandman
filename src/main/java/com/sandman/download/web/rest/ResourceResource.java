package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.domain.BaseDto;
import com.sandman.download.service.ResourceService;
import com.sandman.download.web.rest.errors.BadRequestAlertException;
import com.sandman.download.web.rest.util.FileUtils;
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
     * POST  /resources : Create a new resource.
     *
     * @param resourceDTO the resourceDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new resourceDTO, or with status 400 (Bad Request) if the resource has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/resources")
    @Timed
    public ResponseEntity<ResourceDTO> createResource(@RequestBody ResourceDTO resourceDTO) throws URISyntaxException {
        log.debug("REST request to save Resource : {}", resourceDTO);
        if (resourceDTO.getId() != null) {
            throw new BadRequestAlertException("A new resource cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResourceDTO result = resourceService.save(resourceDTO);
        return ResponseEntity.created(new URI("/api/resources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * post : upload file
     * */
    @PostMapping("/uploadResource")
    @Timed
    public String uploadResource(@RequestParam("file")MultipartFile file) throws IOException {
        if(file.isEmpty()){
            return "上传的文件为空！";
        }
        String fileName = file.getOriginalFilename();
        String filePath = "C:\\Test\\";
        String suffixName = FileUtils.getSuffixNameByFileName(fileName);
        log.info("fileName:{}",fileName);
        log.info("suffixName:{}",suffixName);
        log.info("fileSize={}",file.getSize());
        //File tempFile = FileUtils.getFileByMultipartFile(file); //文件转换必须放到service中，因为转换后。原multiPartFile会删除

        //System.out.println("tempFile fileName:::::::::" + tempFile.getAbsolutePath());
        resourceService.uploadRes(new ResourceDTO(),file);
        if(FileUtils.uploadFile(file.getBytes(),filePath,fileName)){
            return "success!";
        }else{
            return "false!";
        }
    }
    @GetMapping("/downloadResource")
    @Timed
    public String downloadResource(HttpServletResponse response){
        String filePath = "http://39.104.80.30/spkIMG/";
        String fileName = "li.jpg";
        File file = new File(filePath + fileName);
        log.info("fileName={}",file.getName());

        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=\"" + FileUtils.getRightFileNameUseCode(fileName) + "\"");// 设置文件名
        FileUtils.download("/var/www/html/spkIMG/",fileName,response);


        return "success";
    }

    /**
     * PUT  /resources : Updates an existing resource.
     *
     * @param resourceDTO the resourceDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated resourceDTO,
     * or with status 400 (Bad Request) if the resourceDTO is not valid,
     * or with status 500 (Internal Server Error) if the resourceDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/resources")
    @Timed
    public ResponseEntity<ResourceDTO> updateResource(@RequestBody ResourceDTO resourceDTO) throws URISyntaxException {
        log.debug("REST request to update Resource : {}", resourceDTO);
        if (resourceDTO.getId() == null) {
            return createResource(resourceDTO);
        }
        ResourceDTO result = resourceService.save(resourceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, resourceDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /resources : get all the resources.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of resources in body
     */
    @GetMapping("/resources")
    @Timed
    public List<ResourceDTO> getAllResources() {
        log.debug("REST request to get all Resources");
        return resourceService.findAll();
        }

    /**
     * GET  /resources/:id : get the "id" resource.
     *
     * @param id the id of the resourceDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the resourceDTO, or with status 404 (Not Found)
     */
    @GetMapping("/resources/{id}")
    @Timed
    public ResponseEntity<ResourceDTO> getResource(@PathVariable Long id) {
        log.debug("REST request to get Resource : {}", id);
        ResourceDTO resourceDTO = resourceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(resourceDTO));
    }

    /**
     * DELETE  /resources/:id : delete the "id" resource.
     *
     * @param id the id of the resourceDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/resources/{id}")
    @Timed
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        log.debug("REST request to delete Resource : {}", id);
        resourceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
    /**
     * post : resource upload method
     * */
    @PostMapping("/upload")
    @Timed
    public BaseDto uploadResource(@RequestBody ResourceDTO resourceDTO,@RequestParam("file")MultipartFile file){
        log.info("resource upload method:{}",resourceDTO);
        ResourceDTO result = resourceService.uploadRes(resourceDTO,file);
        BaseDto baseDto = new BaseDto(200,"上传成功!",result);
        return baseDto;
    }
}
