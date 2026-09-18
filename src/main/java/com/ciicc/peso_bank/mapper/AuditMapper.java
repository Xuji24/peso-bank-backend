package com.ciicc.peso_bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ciicc.peso_bank.dto.AuditDto;
import com.ciicc.peso_bank.entity.Audit;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    @Mapping(target = "userId", source = "user.userId")
    AuditDto toDto(Audit audit);
}
