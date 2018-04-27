package com.sandman.download.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.sandman.download.domain.BaseDto;
import com.sandman.download.service.ValidateCodeService;
import com.sandman.download.web.rest.errors.BadRequestAlertException;
import com.sandman.download.web.rest.util.HeaderUtil;
import com.sandman.download.service.dto.ValidateCodeDTO;
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
 * REST controller for managing ValidateCode.
 */
@RestController
@RequestMapping("/api/sandman/v1/validateCode")
public class ValidateCodeResource {

    private final Logger log = LoggerFactory.getLogger(ValidateCodeResource.class);

    private static final String ENTITY_NAME = "validateCode";

    private final ValidateCodeService validateCodeService;

    public ValidateCodeResource(ValidateCodeService validateCodeService) {
        this.validateCodeService = validateCodeService;
    }

    /**
     * POST  sendValidateCode : send a new validateCode.
     */
    @PostMapping("/sendValidateCode")
    @Timed
    public BaseDto sendValidateCode(@RequestBody ValidateCodeDTO validateCodeDTO){
        log.info("send a code to {}",validateCodeDTO.getContact());
        return validateCodeService.sendValidateCode(validateCodeDTO);
    }
}
