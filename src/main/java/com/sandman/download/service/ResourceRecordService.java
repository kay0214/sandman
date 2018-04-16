package com.sandman.download.service;

import com.sandman.download.domain.ResourceRecord;
import com.sandman.download.repository.ResourceRecordRepository;
import com.sandman.download.service.dto.ResourceRecordDTO;
import com.sandman.download.service.mapper.ResourceRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing ResourceRecord.
 */
@Service
@Transactional
public class ResourceRecordService {

    private final Logger log = LoggerFactory.getLogger(ResourceRecordService.class);

    private final ResourceRecordRepository resourceRecordRepository;

    private final ResourceRecordMapper resourceRecordMapper;

    public ResourceRecordService(ResourceRecordRepository resourceRecordRepository, ResourceRecordMapper resourceRecordMapper) {
        this.resourceRecordRepository = resourceRecordRepository;
        this.resourceRecordMapper = resourceRecordMapper;
    }

    /**
     * Save a resourceRecord.
     *
     * @param resourceRecordDTO the entity to save
     * @return the persisted entity
     */
    public ResourceRecordDTO save(ResourceRecordDTO resourceRecordDTO) {
        log.debug("Request to save ResourceRecord : {}", resourceRecordDTO);
        ResourceRecord resourceRecord = resourceRecordMapper.toEntity(resourceRecordDTO);
        resourceRecord = resourceRecordRepository.save(resourceRecord);
        return resourceRecordMapper.toDto(resourceRecord);
    }

    /**
     * Get all the resourceRecords.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ResourceRecordDTO> findAll() {
        log.debug("Request to get all ResourceRecords");
        return resourceRecordRepository.findAll().stream()
            .map(resourceRecordMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one resourceRecord by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ResourceRecordDTO findOne(Long id) {
        log.debug("Request to get ResourceRecord : {}", id);
        ResourceRecord resourceRecord = resourceRecordRepository.findOne(id);
        return resourceRecordMapper.toDto(resourceRecord);
    }

    /**
     * Delete the resourceRecord by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ResourceRecord : {}", id);
        resourceRecordRepository.delete(id);
    }
}
