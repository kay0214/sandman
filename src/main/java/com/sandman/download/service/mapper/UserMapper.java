package com.sandman.download.service.mapper;

import com.sandman.download.domain.*;
import com.sandman.download.service.dto.UserDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity User and its DTO UserDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserMapper extends EntityMapper<UserDTO, User> {



    default User fromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
