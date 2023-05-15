package com.dom.reactspringapp.mapper;

import com.dom.reactspringapp.dto.UserDTO;
import com.dom.reactspringapp.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO map(UserEntity userEntity);

    @InheritInverseConfiguration
    UserEntity map(UserDTO userDTO);

}
