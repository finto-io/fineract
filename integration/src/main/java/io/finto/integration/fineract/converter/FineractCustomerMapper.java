package io.finto.integration.fineract.converter;

import io.finto.domain.customer.CustomerMobileNumber;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractCustomerMapper {

    FineractCustomerMapper INSTANCE = Mappers.getMapper(FineractCustomerMapper.class);

    default CustomerMobileNumber toCustomerMobileNumber(String mobileNo) {
        return mobileNo != null ? CustomerMobileNumber.builder().mobileNumber(mobileNo).build() : CustomerMobileNumber.builder().mobileNumber("").build();
    }

}