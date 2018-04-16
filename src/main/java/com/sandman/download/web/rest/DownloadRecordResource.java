package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.service.DownloadRecordService;
import com.sandman.download.web.rest.errors.BadRequestAlertException;
import com.sandman.download.web.rest.util.HeaderUtil;
import com.sandman.download.service.dto.DownloadRecordDTO;
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
 * REST controller for managing DownloadRecord.
 */
@RestController
@RequestMapping("/api/sandman/v1/downloadRecord")
public class DownloadRecordResource {

    private final Logger log = LoggerFactory.getLogger(DownloadRecordResource.class);

    private static final String ENTITY_NAME = "downloadRecord";

    private final DownloadRecordService downloadRecordService;

    public DownloadRecordResource(DownloadRecordService downloadRecordService) {
        this.downloadRecordService = downloadRecordService;
    }

    /**
     * POST  /download-records : Create a new downloadRecord.
     *
     * @param downloadRecordDTO the downloadRecordDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new downloadRecordDTO, or with status 400 (Bad Request) if the downloadRecord has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/download-records")
    @Timed
    public ResponseEntity<DownloadRecordDTO> createDownloadRecord(@RequestBody DownloadRecordDTO downloadRecordDTO) throws URISyntaxException {
        log.debug("REST request to save DownloadRecord : {}", downloadRecordDTO);
        if (downloadRecordDTO.getId() != null) {
            throw new BadRequestAlertException("A new downloadRecord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DownloadRecordDTO result = downloadRecordService.save(downloadRecordDTO);
        return ResponseEntity.created(new URI("/api/download-records/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /download-records : Updates an existing downloadRecord.
     *
     * @param downloadRecordDTO the downloadRecordDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated downloadRecordDTO,
     * or with status 400 (Bad Request) if the downloadRecordDTO is not valid,
     * or with status 500 (Internal Server Error) if the downloadRecordDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/download-records")
    @Timed
    public ResponseEntity<DownloadRecordDTO> updateDownloadRecord(@RequestBody DownloadRecordDTO downloadRecordDTO) throws URISyntaxException {
        log.debug("REST request to update DownloadRecord : {}", downloadRecordDTO);
        if (downloadRecordDTO.getId() == null) {
            return createDownloadRecord(downloadRecordDTO);
        }
        DownloadRecordDTO result = downloadRecordService.save(downloadRecordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, downloadRecordDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /download-records : get all the downloadRecords.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of downloadRecords in body
     */
    @GetMapping("/download-records")
    @Timed
    public List<DownloadRecordDTO> getAllDownloadRecords() {
        log.debug("REST request to get all DownloadRecords");
        return downloadRecordService.findAll();
        }

    /**
     * GET  /download-records/:id : get the "id" downloadRecord.
     *
     * @param id the id of the downloadRecordDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the downloadRecordDTO, or with status 404 (Not Found)
     */
    @GetMapping("/download-records/{id}")
    @Timed
    public ResponseEntity<DownloadRecordDTO> getDownloadRecord(@PathVariable Long id) {
        log.debug("REST request to get DownloadRecord : {}", id);
        DownloadRecordDTO downloadRecordDTO = downloadRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(downloadRecordDTO));
    }

    /**
     * DELETE  /download-records/:id : delete the "id" downloadRecord.
     *
     * @param id the id of the downloadRecordDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/download-records/{id}")
    @Timed
    public ResponseEntity<Void> deleteDownloadRecord(@PathVariable Long id) {
        log.debug("REST request to delete DownloadRecord : {}", id);
        downloadRecordService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
