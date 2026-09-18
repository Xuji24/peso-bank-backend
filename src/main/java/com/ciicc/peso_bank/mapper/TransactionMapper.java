package com.ciicc.peso_bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ciicc.peso_bank.dto.TransactionDto;
import com.ciicc.peso_bank.entity.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "accountId", source = "account.accountId")
    TransactionDto toDto(Transaction transaction);
}
