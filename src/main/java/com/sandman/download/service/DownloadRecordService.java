package com.sandman.download.service;

import com.sandman.download.domain.DownloadRecord;
import com.sandman.download.repository.DownloadRecordRepository;
import com.sandman.download.service.dto.DownloadRecordDTO;
import com.sandman.download.service.mapper.DownloadRecordMapper;
import com.sandman.download.web.rest.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing DownloadRecord.
 */
@Service
@Transactional
public class DownloadRecordService {

    private final Logger log = LoggerFactory.getLogger(DownloadRecordService.class);

    private final DownloadRecordRepository downloadRecordRepository;

    private final DownloadRecordMapper downloadRecordMapper;

    public DownloadRecordService(DownloadRecordRepository downloadRecordRepository, DownloadRecordMapper downloadRecordMapper) {
        this.downloadRecordRepository = downloadRecordRepository;
        this.downloadRecordMapper = downloadRecordMapper;
    }

    /**
     * Save a downloadRecord.
     *
     * @return the persisted entity
     */
    public DownloadRecordDTO save(Long userId,Long resId) {
        DownloadRecordDTO downloadRecordDTO = new DownloadRecordDTO();
        downloadRecordDTO.setUserId(userId);
        downloadRecordDTO.setResId(resId);
        downloadRecordDTO.setRecordTime(DateUtils.getLongTime());
        DownloadRecord downloadRecord = downloadRecordMapper.toEntity(downloadRecordDTO);
        downloadRecord = downloadRecordRepository.save(downloadRecord);
        return downloadRecordMapper.toDto(downloadRecord);
    }


    /**
     * Get all the downloadRecords.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<DownloadRecordDTO> findAll() {
        log.debug("Request to get all DownloadRecords");
        return downloadRecordRepository.findAll().stream()
            .map(downloadRecordMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one downloadRecord by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public DownloadRecordDTO findOne(Long id) {
        log.debug("Request to get DownloadRecord : {}", id);
        DownloadRecord downloadRecord = downloadRecordRepository.findOne(id);
        return downloadRecordMapper.toDto(downloadRecord);
    }

    /**
     * Delete the downloadRecord by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete DownloadRecord : {}", id);
        downloadRecordRepository.delete(id);
    }
}
