package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.ResourceRecordDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ResourceRecord and its DTO ResourceRecordDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ResourceRecordMapper extends EntityMapper<ResourceRecordDTO, ResourceRecord> {



    default ResourceRecord fromId(Long id) {
        if (id == null) {
            return null;
        }
        ResourceRecord resourceRecord = new ResourceRecord();
        resourceRecord.setId(id);
        return resourceRecord;
    }
}
