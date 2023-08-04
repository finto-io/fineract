package io.finto.integration.fineract.converter;

import io.finto.domain.customer.Address;
import io.finto.domain.customer.Profession;
import io.finto.domain.customer.UpdatingCustomer;
import io.finto.fineract.sdk.models.PostClientClientIdAddressesRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface FineractAddressMapper {
    FineractAddressMapper INSTANCE = Mappers.getMapper(FineractAddressMapper.class);

    @Mapping(target = "type", expression = "java(io.finto.fineract.sdk.Constants.RESIDENCE_ADDRESS_CODE_NAME)")
    Address toResidenceAddressDomain(UpdatingCustomer request);

    @Mapping(target = "type", expression = "java(io.finto.fineract.sdk.Constants.WORK_ADDRESS_CODE_NAME)")
    @Mapping(target = "addressLine1", source = "address1")
    @Mapping(target = "addressLine2", source = "address2")
    @Mapping(target = "addressLine3", source = "address3")
    @Mapping(target = "country", source = "country")
    Address toWorkAddressDomain(Profession request);

    @Mapping(target = "addressLine1", source = "request.addressLine1")
    @Mapping(target = "addressLine2", source = "request.addressLine2")
    @Mapping(target = "addressLine3", source = "request.addressLine3")
    @Mapping(target = "street", source = "request.street")
    @Mapping(target = "city", source = "request.city")
    @Mapping(target = "countryId", source = "countryId")
    @Mapping(target = "postalCode", source = "postalCodeId")
    @Mapping(target = "isActive", constant = "true")
    PostClientClientIdAddressesRequest toCreateAddressDto(Address request, Long countryId, Long postalCodeId);

}
