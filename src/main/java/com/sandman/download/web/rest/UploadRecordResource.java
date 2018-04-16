package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.service.UploadRecordService;
import com.sandman.download.web.rest.errors.BadRequestAlertException;
import com.sandman.download.web.rest.util.HeaderUtil;
import com.sandman.download.service.dto.UploadRecordDTO;
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
 * REST controller for managing UploadRecord.
 */
@RestController
@RequestMapping("/api/sandman/v1/uploadRecord")
public class UploadRecordResource {

    private final Logger log = LoggerFactory.getLogger(UploadRecordResource.class);

    private static final String ENTITY_NAME = "uploadRecord";

    private final UploadRecordService uploadRecordService;

    public UploadRecordResource(UploadRecordService uploadRecordService) {
        this.uploadRecordService = uploadRecordService;
    }

    /**
     * POST  /upload-records : Create a new uploadRecord.
     *
     * @param uploadRecordDTO the uploadRecordDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new uploadRecordDTO, or with status 400 (Bad Request) if the uploadRecord has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/upload-records")
    @Timed
    public ResponseEntity<UploadRecordDTO> createUploadRecord(@RequestBody UploadRecordDTO uploadRecordDTO) throws URISyntaxException {
        log.debug("REST request to save UploadRecord : {}", uploadRecordDTO);
        if (uploadRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new uploadRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UploadRecordDTO result = uploadRecordService.save(uploadRecordDTO);
        return ResponseEntity.created(new URI("/api/upload-records/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /upload-records : Updates an existing uploadRecord.
     *
     * @param uploadRecordDTO the uploadRecordDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated uploadRecordDTO,
     * or with status 400 (Bad Request) if the uploadRecordDTO is not valid,
     * or with status 500 (Internal Server Error) if the uploadRecordDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/upload-records")
    @Timed
    public ResponseEntity<UploadRecordDTO> updateUploadRecord(@RequestBody UploadRecordDTO uploadRecordDTO) throws URISyntaxException {
        log.debug("REST request to update UploadRecord : {}", uploadRecordDTO);
        if (uploadRecordDTO.getId() == null) {
            return createUploadRecord(uploadRecordDTO);
        }
        UploadRecordDTO result = uploadRecordService.save(uploadRecordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, uploadRecordDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /upload-records : get all the uploadRecords.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of uploadRecords in body
     */
    @GetMapping("/upload-records")
    @Timed
    public List<UploadRecordDTO> getAllUploadRecords() {
        log.debug("REST request to get all UploadRecords");
        return uploadRecordService.findAll();
        }

    /**
     * GET  /upload-records/:id : get the "id" uploadRecord.
     *
     * @param id the id of the uploadRecordDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the uploadRecordDTO, or with status 404 (Not Found)
     */
    @GetMapping("/upload-records/{id}")
    @Timed
    public ResponseEntity<UploadRecordDTO> getUploadRecord(@PathVariable Long id) {
        log.debug("REST request to get UploadRecord : {}", id);
        UploadRecordDTO uploadRecordDTO = uploadRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(uploadRecordDTO));
    }

    /**
     * DELETE  /upload-records/:id : delete the "id" uploadRecord.
     *
     * @param id the id of the uploadRecordDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/upload-records/{id}")
    @Timed
    public ResponseEntity<Void> deleteUploadRecord(@PathVariable Long id) {
        log.debug("REST request to delete UploadRecord : {}", id);
        uploadRecordService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
