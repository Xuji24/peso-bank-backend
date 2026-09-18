package com.ciicc.peso_bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ciicc.peso_bank.dto.AccountDto;
import com.ciicc.peso_bank.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "active", source = "active")
    @Mapping(target = "userId", source = "user.userId")
    AccountDto toDto(Account account);
}
