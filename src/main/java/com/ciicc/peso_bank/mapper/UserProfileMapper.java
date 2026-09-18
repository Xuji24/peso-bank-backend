package com.ciicc.peso_bank.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.ciicc.peso_bank.dto.UserProfileDto;
import com.ciicc.peso_bank.dto.UserProfileUpdate;
import com.ciicc.peso_bank.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileDto toDto(UserProfile profile);

    UserProfile toEntity(UserProfileDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserProfileUpdate dto, @MappingTarget UserProfile entity);
}
