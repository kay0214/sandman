package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.domain.BaseDto;
import com.sandman.download.domain.UploadRecord;
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
import java.util.Map;
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
     * GET : get all the uploadRecords.
     */
    @GetMapping("/getAllRecords")
    @Timed
    public BaseDto getAllUploadRecords(Integer pageNumber, Integer size) {
        log.debug("REST request to get all UploadRecords");
        Map data = uploadRecordService.getAllUploadRecords(pageNumber, size);
        return new BaseDto(200,"请求成功!",data);
    }

    /**
     * GET  /upload-records/:id : get the "id" uploadRecord.
     *
     * @param id the id of the uploadRecordDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the uploadRecordDTO, or with status 404 (Not Found)
     */
/*    @GetMapping("/upload-records/{id}")
    @Timed
    public ResponseEntity<UploadRecordDTO> getUploadRecord(@PathVariable Long id) {
        log.debug("REST request to get UploadRecord : {}", id);
        UploadRecordDTO uploadRecordDTO = uploadRecordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(uploadRecordDTO));
    }*/

}
