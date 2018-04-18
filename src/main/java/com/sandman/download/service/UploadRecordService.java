package com.sandman.download.service;

import com.sandman.download.domain.UploadRecord;
import com.sandman.download.repository.UploadRecordRepository;
import com.sandman.download.service.dto.UploadRecordDTO;
import com.sandman.download.service.mapper.UploadRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing UploadRecord.
 */
@Service
@Transactional
public class UploadRecordService {

    private final Logger log = LoggerFactory.getLogger(UploadRecordService.class);

    private final UploadRecordRepository uploadRecordRepository;

    private final UploadRecordMapper uploadRecordMapper;

    public UploadRecordService(UploadRecordRepository uploadRecordRepository, UploadRecordMapper uploadRecordMapper) {
        this.uploadRecordRepository = uploadRecordRepository;
        this.uploadRecordMapper = uploadRecordMapper;
    }

    /**
     * Save a uploadRecord.
     *
     * @param uploadRecordDTO the entity to save
     * @return the persisted entity
     */
    public UploadRecordDTO save(UploadRecordDTO uploadRecordDTO) {
        log.debug("Request to save UploadRecord : {}", uploadRecordDTO);
        UploadRecord uploadRecord = uploadRecordMapper.toEntity(uploadRecordDTO);
        uploadRecord = uploadRecordRepository.save(uploadRecord);
        return uploadRecordMapper.toDto(uploadRecord);
    }

    /**
     * Get all the uploadRecords.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<UploadRecordDTO> findAll() {
        log.debug("Request to get all UploadRecords");
        return uploadRecordRepository.findAll().stream()
            .map(uploadRecordMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one uploadRecord by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public UploadRecordDTO findOne(Long id) {
        log.debug("Request to get UploadRecord : {}", id);
        UploadRecord uploadRecord = uploadRecordRepository.findOne(id);
        return uploadRecordMapper.toDto(uploadRecord);
    }

    /**
     * Delete the uploadRecord by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete UploadRecord : {}", id);
        uploadRecordRepository.delete(id);
    }


}
