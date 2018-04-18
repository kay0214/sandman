package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.domain.BaseDto;
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
    public void downloadResource(Long resId,HttpServletResponse response){
        log.info("用户下载资源id:{}",resId);
        try {
            resourceService.downloadRes(resId, response);
        } catch (IOException e) {
            System.out.println(e);
        }
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
    @PostMapping("/updateResource")
    @Timed
    public BaseDto updateResource(ResourceDTO resourceDTO){
        log.info("resourceDto:{}",resourceDTO.toString());
        return resourceService.updateResource(resourceDTO);
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
}
