package com.sandman.download.service;

import com.sandman.download.common.repository.PageableTools;
import com.sandman.download.common.repository.SortDto;
import com.sandman.download.domain.DownloadRecord;
import com.sandman.download.domain.UploadRecord;
import com.sandman.download.repository.DownloadRecordRepository;
import com.sandman.download.service.dto.DownloadRecordDTO;
import com.sandman.download.service.mapper.DownloadRecordMapper;
import com.sandman.download.web.rest.util.DateUtils;
import com.sandman.download.web.rest.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    public Map getAllDownloadRecords(Integer pageNumber, Integer size) {
        log.debug("Request to get all DownloadRecords");
        pageNumber = (pageNumber==null || pageNumber<1)?1:pageNumber;
        size = (size==null || size<0)?10:size;
        Pageable pageable = PageableTools.basicPage(pageNumber,size,new SortDto("desc","recordTime"));
        Page downloadRecordPage = downloadRecordRepository.findAll(pageable);
        List<DownloadRecord> downloadRecordList = downloadRecordPage.getContent();
        downloadRecordList.forEach(downloadRecord -> {
            System.out.println(downloadRecord.getRes().getResSize());
        });
        Map data = new HashMap();
        data.put("totalRow",downloadRecordPage.getTotalElements());
        data.put("totalPage",downloadRecordPage.getTotalPages());
        data.put("currentPage",downloadRecordPage.getNumber()+1);//默认0就是第一页
        data.put("resourceList",getFileSizeHaveUnit(downloadRecordList));

        return data;
    }
    /**
     * 资源大小：存入数据库的时候统一以byte为单位，取出来给前端的时候要做规范 -> 转换成以 B,KB,MB,GB为单位
     * */
    public List getFileSizeHaveUnit(List<DownloadRecord> resourceList){
        resourceList.forEach(resource -> {//这里必须捕获异常，否则如果size一样的话，会抛出异常
            try{
                String size = resource.getRes().getResSize();
                resource.getRes().setResSize(FileUtils.getFileSize(Long.parseLong(size)));
            }catch(Exception e){
                String size = resource.getRes().getResSize();
                resource.getRes().setResSize(size);
            }

        });
        return resourceList;
    }
    /**
     * Delete the downloadRecord by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete UploadRecord : {}", id);
        downloadRecordRepository.delete(id);
    }
}
