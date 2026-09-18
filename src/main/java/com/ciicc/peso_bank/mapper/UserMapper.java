package com.ciicc.peso_bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ciicc.peso_bank.dto.UserCreateRequest;
import com.ciicc.peso_bank.dto.UserDto;
import com.ciicc.peso_bank.entity.User;

@Mapper(componentModel = "spring", uses = UserProfileMapper.class)
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toEntity(UserCreateRequest request);
}
