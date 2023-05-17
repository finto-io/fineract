package io.finto.integration.fineract.converter;

import io.finto.integration.fineract.domain.TransactionsStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractTransactionMapper {

    FineractTransactionMapper INSTANCE = Mappers.getMapper(FineractTransactionMapper.class);

    @ValueMapping(source = "BLOCKED", target = "block")
    @ValueMapping(source = "UNBLOCKED", target = "unblock")
    String mapStatusToCommand(TransactionsStatus status);

}


