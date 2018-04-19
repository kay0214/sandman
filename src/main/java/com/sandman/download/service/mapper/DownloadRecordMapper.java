package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.DownloadRecordDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity DownloadRecord and its DTO DownloadRecordDTO.
 */
@Mapper(componentModel = "spring", uses = {ResourceMapper.class})
public interface DownloadRecordMapper extends EntityMapper<DownloadRecordDTO, DownloadRecord> {

    @Mapping(source = "res.id", target = "resId")
    DownloadRecordDTO toDto(DownloadRecord downloadRecord);

    @Mapping(source = "resId", target = "res")
    DownloadRecord toEntity(DownloadRecordDTO downloadRecordDTO);

    default DownloadRecord fromId(Long id) {
        if (id == null) {
            return null;
        }
        DownloadRecord downloadRecord = new DownloadRecord();
        downloadRecord.setId(id);
        return downloadRecord;
    }
}
