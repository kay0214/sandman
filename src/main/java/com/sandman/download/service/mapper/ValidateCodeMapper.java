package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.ValidateCodeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity ValidateCode and its DTO ValidateCodeDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ValidateCodeMapper extends EntityMapper<ValidateCodeDTO, ValidateCode> {



    default ValidateCode fromId(Long id) {
        if (id == null) {
            return null;
        }
        ValidateCode validateCode = new ValidateCode();
        validateCode.setId(id);
        return validateCode;
    }
}
