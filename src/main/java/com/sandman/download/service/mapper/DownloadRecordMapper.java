package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.DownloadRecordDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity DownloadRecord and its DTO DownloadRecordDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DownloadRecordMapper extends EntityMapper<DownloadRecordDTO, DownloadRecord> {



    default DownloadRecord fromId(Long id) {
        if (id == null) {
            return null;
        }
        DownloadRecord downloadRecord = new DownloadRecord();
        downloadRecord.setId(id);
        return downloadRecord;
    }
}
