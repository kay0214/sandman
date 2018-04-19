package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.UploadRecordDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity UploadRecord and its DTO UploadRecordDTO.
 */
@Mapper(componentModel = "spring", uses = {ResourceMapper.class})
public interface UploadRecordMapper extends EntityMapper<UploadRecordDTO, UploadRecord> {

    @Mapping(source = "res.id", target = "resId")
    UploadRecordDTO toDto(UploadRecord uploadRecord);

    @Mapping(source = "resId", target = "res")
    UploadRecord toEntity(UploadRecordDTO uploadRecordDTO);

    default UploadRecord fromId(Long id) {
        if (id == null) {
            return null;
        }
        UploadRecord uploadRecord = new UploadRecord();
        uploadRecord.setId(id);
        return uploadRecord;
    }
}
