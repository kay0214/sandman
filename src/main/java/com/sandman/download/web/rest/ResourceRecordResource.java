package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.service.ResourceRecordService;
import com.sandman.download.web.rest.errors.BadRequestAlertException;
import com.sandman.download.web.rest.util.HeaderUtil;
import com.sandman.download.service.dto.ResourceRecordDTO;
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
 * REST controller for managing ResourceRecord.
 */
@RestController
@RequestMapping("/api/sandman/v1/resourceRecord")
public class ResourceRecordResource {

    private final Logger log = LoggerFactory.getLogger(ResourceRecordResource.class);

    private static final String ENTITY_NAME = "resourceRecord";

    private final ResourceRecordService resourceRecordService;

    public ResourceRecordResource(ResourceRecordService resourceRecordService) {
        this.resourceRecordService = resourceRecordService;
    }

    /**
     * POST  /resource-records : Create a new resourceRecord.
     *
     * @param resourceRecordDTO the resourceRecordDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new resourceRecordDTO, or with status 400 (Bad Request) if the resourceRecord has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/resource-records")
    @Timed
    public ResponseEntity<ResourceRecordDTO> createResourceRecord(@RequestBody ResourceRecordDTO resourceRecordDTO) throws URISyntaxException {
        log.debug("REST request to save ResourceRecord : {}", resourceRecordDTO);
        if (resourceRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new resourceRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ResourceRecordDTO result = resourceRecordService.save(resourceRecordDTO);
        return ResponseEntity.created(new URI("/api/resource-records/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /resource-records : Updates an existing resourceRecord.
     *
     * @param resourceRecordDTO the resourceRecordDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated resourceRecordDTO,
     * or with status 400 (Bad Request) if the resourceRecordDTO is not valid,
     * or with status 500 (Internal Server Error) if the resourceRecordDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/resource-records")
    @Timed
    public ResponseEntity<ResourceRecordDTO> updateResourceRecord(@RequestBody ResourceRecordDTO resourceRecordDTO) throws URISyntaxException {
        log.debug("REST request to update ResourceRecord : {}", resourceRecordDTO);
        if (resourceRecordDTO.getId() == null) {
            return createResourceRecord(resourceRecordDTO);
        }
        ResourceRecordDTO result = resourceRecordService.save(resourceRecordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, resourceRecordDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /resource-records : get all the resourceRecords.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of resourceRecords in body
     */
    @GetMapping("/resource-records")
    @Timed
    public List<ResourceRecordDTO> getAllResourceRecords() {
        log.debug("REST request to get all ResourceRecords");
        return resourceRecordService.findAll();
        }

    /**
     * GET  /resource-records/:id : get the "id" resourceRecord.
     *
     * @param id the id of the resourceRecordDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the resourceRecordDTO, or with status 404 (Not Found)
     */
    @GetMapping("/resource-records/{id}")
    @Timed
    public ResponseEntity<ResourceRecordDTO> getResourceRecord(@PathVariable Long id) {
        log.debug("REST request to get ResourceRecord : {}", id);
        ResourceRecordDTO resourceRecordDTO = resourceRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(resourceRecordDTO));
    }

    /**
     * DELETE  /resource-records/:id : delete the "id" resourceRecord.
     *
     * @param id the id of the resourceRecordDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/resource-records/{id}")
    @Timed
    public ResponseEntity<Void> deleteResourceRecord(@PathVariable Long id) {
        log.debug("REST request to delete ResourceRecord : {}", id);
        resourceRecordService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
