package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.UploadRecordDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UploadRecord and its DTO UploadRecordDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UploadRecordMapper extends EntityMapper<UploadRecordDTO, UploadRecord> {



    default UploadRecord fromId(Long id) {
        if (id == null) {
            return null;
        }
        UploadRecord uploadRecord = new UploadRecord();
        uploadRecord.setId(id);
        return uploadRecord;
    }
}
