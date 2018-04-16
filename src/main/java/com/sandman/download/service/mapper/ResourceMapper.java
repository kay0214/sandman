package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.ResourceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Resource and its DTO ResourceDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ResourceMapper extends EntityMapper<ResourceDTO, Resource> {



    default Resource fromId(Long id) {
        if (id == null) {
            return null;
        }
        Resource resource = new Resource();
        resource.setId(id);
        return resource;
    }
}
