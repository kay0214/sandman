package com.sandman.download.service;

import com.sandman.download.common.repository.PageableTools;
import com.sandman.download.common.repository.SortDto;
import com.sandman.download.domain.Resource;
import com.sandman.download.domain.UploadRecord;
import com.sandman.download.repository.UploadRecordRepository;
import com.sandman.download.service.dto.UploadRecordDTO;
import com.sandman.download.service.mapper.UploadRecordMapper;
import com.sandman.download.web.rest.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
     * Get all the uploadRecords page.
     */
    @Transactional(readOnly = true)
    public Map getAllUploadRecords(Integer pageNumber, Integer size) {
        log.debug("Request to get all UploadRecords");
        pageNumber = (pageNumber==null || pageNumber<1)?1:pageNumber;
        size = (size==null || size<0)?10:size;
        Pageable pageable = PageableTools.basicPage(pageNumber,size,new SortDto("desc","recordTime"));
        Page<UploadRecord> uploadRecordPage = uploadRecordRepository.findAll(pageable);
        Map data = new HashMap();
        data.put("totalRow",uploadRecordPage.getTotalElements());
        data.put("totalPage",uploadRecordPage.getTotalPages());
        data.put("currentPage",uploadRecordPage.getNumber()+1);//默认0就是第一页
        data.put("resourceList",getFileSizeHaveUnit(uploadRecordPage.getContent()));

        return data;
    }
    /**
     * 资源大小：存入数据库的时候统一以byte为单位，取出来给前端的时候要做规范 -> 转换成以 B,KB,MB,GB为单位
     * */
    public List getFileSizeHaveUnit(List<UploadRecord> resourceList){
        for(UploadRecord uploadRecord:resourceList){
            String size = uploadRecord.getRes().getResSize();
            uploadRecord.getRes().setResSize(FileUtils.getFileSize(Long.parseLong(size)));
        }
/*        resourceList.forEach(resource -> {
            String size = resource.getRes().getResSize();
            resource.getRes().setResSize(FileUtils.getFileSize(Long.parseLong(size)));
        });*/
        return resourceList;
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
