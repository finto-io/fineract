package io.finto.integration.fineract.converter;

import io.finto.domain.customer.*;
import io.finto.fineract.sdk.models.*;
import io.finto.integration.fineract.dto.CustomerAdditionalFieldsDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.finto.fineract.sdk.Constants.*;
import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FineractCustomerMapperTest {

    private final FineractCustomerMapper mapper = FineractCustomerMapper.INSTANCE;
    private Long genderId = 2L;
    private Long countryId = 3L;
    private Long profCountryId = 4L;
    private Long residenceAddressTypeId = 5L;
    private Long workAddressTypeId = 6L;
    private Long postalCodeId = 7L;

    private OpeningCustomer generateOpeningCustomer() {
        return OpeningCustomer.builder()
                .officeId(1)
                .sName("KYC-FINTO-224")
                .branch("001")
                .swiftCD("")
                .cType("I")
                .arabName("ZAID MOHD KAMEL FAHIM al farekh")
                .ccaTeg("601DC")
                .staff(false)
                .nationality("PS")
                .nationId("9851026853")
                .uidName("Passport")
                .uidValue("1683630858")
                .country("JO")
                .country1("JO")
                .addressLine1("883")
                .addressLine2("zbdbdb")
                .addressLine3("")
                .streetName("zbdbdb")
                .city("1865")
                .buildingNumber("883")
                .poBox("")
                .postalAreaDescription1("")
                .postalCode("")
                .deceased(false)
                .frozen(false)
                .mt920(false)
                .language("ARB")
                .trsRyCust(false)
                .relPricing(false)
                .personalData(io.finto.domain.customer.PersonalData.builder()
                        .title("MR.")
                        .firstName("زيد")
                        .middleName("محمد كامل فهيم")
                        .lastName("الفرخ")
                        .dateOfBirth("1985-09-13")
                        .mobileNumber("00962777777777")
                        .nationId("9851026853")
                        .pptExpDate("")
                        .pptNumber("")
                        .resStatus("R")
                        .sex("M")
                        .tel("")
                        .guardian("")
                        .minor("")
                        .domestic(io.finto.domain.customer.Domestic.builder()
                                .accomoDate("S")
                                .depoth("0")
                                .eduStat("U")
                                .maritalStat("M")
                                .spName("DUNYA A GH BASHITI")
                                .build())
                        .prof(io.finto.domain.customer.Profession.builder()
                                .country("JO")
                                .address1("a1")
                                .address2("a2")
                                .address3("a3")
                                .amtCurrency("JOD")
                                .currDesig("")
                                .currEmp("")
                                .email("ahmed.s+18@sitech.me")
                                .empTenure("")
                                .fax("")
                                .salary("")
                                .phone("")
                                .build())
                        .build()
                )
                .udfDetails(Map.of(io.finto.domain.customer.UdfName.SELF_REGISTRATION_EMAIL, "ahmed.s+18@sitech.me", UdfName.RESTRICTED_CIF, "1"))
                .identity(io.finto.domain.customer.Identity.builder()
                        .userId("2067f9dc-6429-47cd-82d3-ca4d313003da")
                        .partnerId("c2529dfb-5cd1-4028-9957-c4e784b1e8e4")
                        .partnerName("string")
                        .build())
                .build();
    }

    private PostClientsRequest generatePostClientsRequest() {
        return PostClientsRequest.builder()
                .submittedOnDate(LocalDate.now().format(DEFAULT_DATE_FORMATTER))
                .active(false)
                .address(List.of(
                        PostClientsAddressRequest.builder()
                                .addressTypeId(residenceAddressTypeId)
                                .addressLine1("883")
                                .addressLine2("zbdbdb")
                                .addressLine3("")
                                .city("1865")
                                .countryId(countryId.intValue())
                                .postalCode(postalCodeId)
                                .isActive(true)
                                .build(),
                        PostClientsAddressRequest.builder()
                                .addressTypeId(workAddressTypeId)
                                .addressLine1("a1")
                                .addressLine2("a2")
                                .addressLine3("a3")
                                .countryId(profCountryId.intValue())
                                .isActive(true)
                                .build()))
                .datatables(List.of(PostClientsDatatable.builder()
                        .registeredTableName(CUSTOMER_ADDITIONAL_FIELDS)
                        .data(PostClientsDatatableData.builder()
                                .locale(LOCALE)
                                .kycId("KYC-FINTO-224")
                                .nationality("PS")
                                .residencyFlag("R")
                                .deceased(false)
                                .dormant(false)
                                .dormantSinceDate(null)
                                .isCustomerRestricted(true)
                                .email("ahmed.s+18@sitech.me")
                                .userId("2067f9dc-6429-47cd-82d3-ca4d313003da")
                                .partnerId("c2529dfb-5cd1-4028-9957-c4e784b1e8e4")
                                .partnerName("string")
                                .build())
                        .build()))
                .dateFormat(DATE_FORMAT_PATTERN)
                .dateOfBirth(LocalDate.parse("1985-09-13", DEFAULT_DATE_FORMATTER))
                .firstname("زيد")
                .middlename("محمد كامل فهيم")
                .lastname("الفرخ")
                .isStaff(false)
                .genderId(genderId)
                .legalFormId(1)
                .locale(LOCALE)
                .mobileNo("00962777777777")
                .officeId(1)
                .build();
    }

    private Customer generateCustomerDomain(){
        return Customer.builder()
                .customerId(CustomerId.of(1L))
                .status("Active")
                .firstName("John")
                .middleName("Viktorovich")
                .lastName("Wick")
                .fullName("John Viktorovich Wick")
                .mobileNumber("002395024")
                .dateOfBirth("1980-10-10")
                .legalForm("I")
                .sex("M")
                .isStaff(false)
                .officeId(1)
                .addresses(List.of(io.finto.domain.customer.Address.builder()
                        .id(1L)
                        .type("Residence Address")
                        .country("JO")
                        .addressLine1("883")
                        .addressLine2("zbdbdb")
                        .addressLine3("")
                        .street("zbdbdb")
                        .stateProvince("whatever")
                        .postalCode("123456")
                        .build()))
                .identifiers(List.of(io.finto.domain.customer.CustomerIdentifier.builder()
                        .id(IdentifierId.of(1))
                        .name("Passport")
                        .value("2324564")
                        .description("whatever")
                        .build()))
                .kycId("KYC-FINTO-224")
                .externalCustomerNumber("externalCustomerNumber")
                .nationality("nationality")
                .residenceFlag("residenceFlag")
                .deceased(false)
                .dormant(false)
                .dormantSinceDate("2023-01-01")
                .isCustomerRestricted(true)
                .externalSource(true)
                .email("email")
                .createdAt("2023-01-01")
                .createdBy("mifos")
                .activatedAt("2023-02-02")
                .activatedBy("mifos")
                .updatedAt("2024-12-12")
                .updatedBy("mifos")
                .userId("2067f9dc-6429-47cd-82d3-ca4d313003da")
                .partnerId("c2529dfb-5cd1-4028-9957-c4e784b1e8e4")
                .partnerName("string")
                .updateFlag("updateFlag")
                .build();
    }

    private GetClientsClientIdResponse generateGetClientsClientIdResponse(){
        return GetClientsClientIdResponse.builder()
                .displayName("John Viktorovich Wick")
                .firstname("John")
                .middlename("Viktorovich")
                .id(1)
                .lastname("Wick")
                .mobileNo("002395024")
                .dateOfBirth(LocalDate.parse("1980-10-10", DEFAULT_DATE_FORMATTER))
                .isStaff(false)
                .officeId(1)
                .legalForm(CommonEnumValue.builder()
                        .id(1)
                        .build())
                .gender(CodeValueData.builder()
                        .name("M")
                        .build())
                .status(GetClientsClientIdStatus.builder()
                        .value("Active")
                        .build())
                .timeline(GetClientsTimeline.builder()
                        .submittedOnDate(LocalDate.parse("2023-01-01", DEFAULT_DATE_FORMATTER))
                        .submittedByUsername("mifos")
                        .activatedOnDate(LocalDate.parse("2023-02-02", DEFAULT_DATE_FORMATTER))
                        .activatedByUsername("mifos")
                        .build())
                .build();
    }

    private GetClientClientIdAddressesResponse generateGetClientClientIdAddressesResponse(){
        return GetClientClientIdAddressesResponse.builder()
                .addressId(1)
                .addressType("Residence Address")
                .addressLine1("883")
                .addressLine2("zbdbdb")
                .addressLine3("")
                .street("zbdbdb")
                .stateName("whatever")
                .countryName("JO")
                .postalCode("123456")
                .build();
    }

    private GetClientsClientIdIdentifiersResponse generateGetClientsClientIdIdentifiersResponse(){
        return GetClientsClientIdIdentifiersResponse.builder()
                .id(1)
                .documentType(GetClientsDocumentType.builder()
                        .name("Passport")
                        .build())
                .documentKey("2324564")
                .description("whatever")
                .build();
    }

    private CustomerAdditionalFieldsDto generateCustomerAdditionalFieldsDto(){
        return CustomerAdditionalFieldsDto.builder()
                .kycId("KYC-FINTO-224")
                .externalCustomerNumber("externalCustomerNumber")
                .nationality("nationality")
                .residenceFlag("residenceFlag")
                .deceased(false)
                .dormant(false)
                .dormantSinceDate("2023-01-01")
                .isCustomerRestricted(true)
                .email("email")
                .externalSource(true)
                .updatedAt(LocalDate.parse("2024-12-12", DEFAULT_DATE_FORMATTER).atStartOfDay())
                .updatedBy("mifos")
                .userId("2067f9dc-6429-47cd-82d3-ca4d313003da")
                .partnerId("c2529dfb-5cd1-4028-9957-c4e784b1e8e4")
                .partnerName("string")
                .updateFlag("updateFlag")
                .build();
    }

    @Test
    void test_toOpeningCustomerDto() {
        var actual = mapper.toOpeningCustomerDto(generateOpeningCustomer(), genderId, countryId, profCountryId, residenceAddressTypeId, workAddressTypeId, postalCodeId);
        var expected = generatePostClientsRequest();
        assertEquals(expected, actual);
    }

    @Test
    void test_toDomain(){
        var actual = mapper.toDomain(
                generateGetClientsClientIdResponse(),
                List.of(generateGetClientClientIdAddressesResponse()),
                List.of(generateGetClientsClientIdIdentifiersResponse()),
                generateCustomerAdditionalFieldsDto()
        );
        var expected = generateCustomerDomain();
        assertEquals(expected, actual);
    }

    @Test
    void testToCustomerMobileNumber_WithNonNullMobileNo() {
        String mobileNo = "123456789";
        CustomerMobileNumber expected = CustomerMobileNumber.builder().mobileNumber(mobileNo).build();

        CustomerMobileNumber result = mapper.toCustomerMobileNumber(mobileNo);

        assertEquals(expected, result);
    }

    @Test
    void testToCustomerMobileNumber_WithNullMobileNo() {
        String mobileNo = null;
        CustomerMobileNumber expected = CustomerMobileNumber.builder().mobileNumber("").build();

        CustomerMobileNumber result = mapper.toCustomerMobileNumber(mobileNo);

        assertEquals(expected, result);
    }

}