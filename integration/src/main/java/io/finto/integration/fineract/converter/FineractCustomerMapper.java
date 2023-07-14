package io.finto.integration.fineract.converter;

import io.finto.domain.customer.*;
import io.finto.fineract.sdk.models.*;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static io.finto.fineract.sdk.Constants.*;
import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FineractCustomerMapper {

    FineractCustomerMapper INSTANCE = Mappers.getMapper(FineractCustomerMapper.class);

    default PostClientsRequest toOpeningCustomerDto(OpeningCustomer request, Long genderId, Long countryId, Long profCountryId,
                                                    Long residenceAddressTypeId, Long workAddressTypeId, Long postalCodeId) {
        return PostClientsRequest.builder()
                .officeId(request.getOfficeId())
                .legalFormId("I".equals(request.getCType()) ? 1 : 2)
                .firstname(request.getPersonalData().getFirstName())
                .middlename(request.getPersonalData().getMiddleName())
                .lastname(request.getPersonalData().getLastName())
                .isStaff(request.getStaff())
                .active(false)
                .mobileNo(request.getPersonalData().getMobileNumber())
                .dateOfBirth(LocalDate.parse(request.getPersonalData().getDateOfBirth(), DEFAULT_DATE_FORMATTER))
                .genderId(genderId)
                .submittedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER))
                .locale(LOCALE)
                .dateFormat(DATE_FORMAT_PATTERN)
                .address(List.of(
                                PostClientsAddressRequest.builder()
                                        .addressTypeId(residenceAddressTypeId)
                                        .addressLine1(request.getAddressLine1())
                                        .addressLine2(request.getAddressLine2())
                                        .addressLine3(request.getAddressLine3())
                                        .city(request.getCity())
                                        .countryId(countryId == null ? null : countryId.intValue())
                                        .postalCode(postalCodeId)
                                        .isActive(true)
                                        .build(),
                                PostClientsAddressRequest.builder()
                                        .addressTypeId(workAddressTypeId)
                                        .addressLine1(request.getPersonalData().getProf() == null ? null : request.getPersonalData().getProf().getAddress1())
                                        .addressLine2(request.getPersonalData().getProf() == null ? null : request.getPersonalData().getProf().getAddress2())
                                        .addressLine3(request.getPersonalData().getProf() == null ? null : request.getPersonalData().getProf().getAddress3())
                                        .countryId(profCountryId == null ? null : profCountryId.intValue())
                                        .isActive(true)
                                        .build()
                        )
                )
                .datatables(List.of(PostClientsDatatable.builder()
                        .registeredTableName(CUSTOMER_ADDITIONAL_FIELDS)
                        .data(PostClientsDatatableData.builder()
                                .locale(LOCALE)
                                .kycId(request.getSName())
                                .nationality(request.getNationality())
                                .residencyFlag(request.getPersonalData().getResStatus())
                                .deceased(request.getDeceased())
                                .dormant(request.getFrozen())
                                .dormantSinceDate(null)
                                .isCustomerRestricted(request.getUdfDetails().containsKey(UdfName.RESTRICTED_CIF)
                                        && !request.getUdfDetails().get(UdfName.RESTRICTED_CIF).equals("0"))
                                .email(request.getUdfDetails().get(UdfName.SELF_REGISTRATION_EMAIL))
                                .userId(request.getIdentity().getUserId())
                                .partnerId(request.getIdentity().getPartnerId())
                                .partnerName(request.getIdentity().getPartnerName())
                                .build())
                        .build()))
                .build();
    }

    @Mapping(target = "documentKey", source = "documentKey")
    @Mapping(target = "documentTypeId", source = "documentTypeId")
    @Mapping(target = "status", constant = "ACTIVE")
    PostClientsClientIdIdentifiersRequest toIdentifierRequestDto(Long documentTypeId, String documentKey);

    default Long toPostalCodeId(String postalCode) {
        return Long.parseLong(postalCode);
    }

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "status", source = "customer.status.value")
    @Mapping(target = "firstName", source = "customer.firstname")
    @Mapping(target = "middleName", source = "customer.middlename")
    @Mapping(target = "lastName", source = "customer.lastname")
    @Mapping(target = "fullName", source = "customer.displayName")
    @Mapping(target = "mobileNumber", source = "customer.mobileNo")
    @Mapping(target = "dateOfBirth", source = "customer.dateOfBirth")
    @Mapping(target = "legalForm", expression = "java(customer.getLegalForm().getId().equals(1) ? \"I\" : \"C\")")
    @Mapping(target = "sex", source = "customer.gender.name")
    @Mapping(target = "isStaff", source = "customer.isStaff")
    @Mapping(target = "officeId", source = "customer.officeId")
    @Mapping(target = "createdAt", source = "customer.timeline.submittedOnDate")
    @Mapping(target = "createdBy", source = "customer.timeline.submittedByUsername")
    @Mapping(target = "activatedBy", source = "customer.timeline.activatedByUsername")
    @Mapping(target = "activatedAt", source = "customer.timeline.activatedOnDate")
    @Mapping(target = "addresses", source = "addresses")
    @Mapping(target = "identifiers", source = "identifiers")
    @Mapping(target = "kycId", source = "additionalFields.kycId")
    @Mapping(target = "externalCustomerNumber", source = "additionalFields.externalCustomerNumber")
    @Mapping(target = "nationality", source = "additionalFields.nationality")
    @Mapping(target = "residenceFlag", source = "additionalFields.residenceFlag")
    @Mapping(target = "deceased", source = "additionalFields.deceased")
    @Mapping(target = "dormant", source = "additionalFields.dormant")
    @Mapping(target = "dormantSinceDate", source = "additionalFields.dormantSinceDate")
    @Mapping(target = "isCustomerRestricted", source = "additionalFields.isCustomerRestricted")
    @Mapping(target = "email", source = "additionalFields.email")
    @Mapping(target = "externalSource", source = "additionalFields.externalSource")
    @Mapping(target = "updatedAt", expression = "java(additionalFields.getUpdatedAt().format(io.finto.fineract.sdk.Constants.DEFAULT_DATE_FORMATTER))")
    @Mapping(target = "updatedBy", source = "additionalFields.updatedBy")
    @Mapping(target = "userId", source = "additionalFields.userId")
    @Mapping(target = "partnerId", source = "additionalFields.partnerId")
    @Mapping(target = "partnerName", source = "additionalFields.partnerName")
    @Mapping(target = "updateFlag", source = "additionalFields.updateFlag")
    Customer toDomain(GetClientsClientIdResponse customer, List<GetClientClientIdAddressesResponse> addresses,
                      List<GetClientsClientIdIdentifiersResponse> identifiers, CustomerAdditionalFieldsDto additionalFields);

    @Mapping(target = "id", source = "addressId")
    @Mapping(target = "type", source = "addressType")
    @Mapping(target = "addressLine1", source = "addressLine1")
    @Mapping(target = "addressLine2", source = "addressLine2")
    @Mapping(target = "addressLine3", source = "addressLine3")
    @Mapping(target = "street", source = "street")
    @Mapping(target = "stateProvince", source = "stateName")
    @Mapping(target = "country", source = "countryName")
    @Mapping(target = "postalCode", source = "postalCode")
    Address toDomain(GetClientClientIdAddressesResponse identifierDto);

    @Mapping(target = "id.value", source = "id")
    @Mapping(target = "name", source = "documentType.name")
    @Mapping(target = "value", source = "documentKey")
    @Mapping(target = "description", source = "description")
    CustomerIdentifier toDomain(GetClientsClientIdIdentifiersResponse identifierDto);

    default CustomerId toCustomerId(Integer id) {
        return CustomerId.of(Long.valueOf(id));
    }

    default IdentifierId toIdentifierId(Integer id) {
        return IdentifierId.of(id);
    }

    default CustomerMobileNumber toCustomerMobileNumber(String mobileNo) {
        return mobileNo != null ? CustomerMobileNumber.builder().mobileNumber(mobileNo).build() : CustomerMobileNumber.builder().mobileNumber("").build();
    }

}