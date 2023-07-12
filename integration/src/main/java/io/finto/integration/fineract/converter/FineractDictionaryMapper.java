package io.finto.integration.fineract.converter;

import io.finto.fineract.sdk.models.GetCodeValuesDataResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractDictionaryMapper {

    FineractDictionaryMapper INSTANCE = Mappers.getMapper(FineractDictionaryMapper.class);

    default Map<Long, String> toKeyValueMap(List<GetCodeValuesDataResponse> responses){
        return responses.stream().collect(Collectors.toMap(
                GetCodeValuesDataResponse::getId,
                GetCodeValuesDataResponse::getName));
    }

}
