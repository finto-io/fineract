package io.finto.integration.fineract.test.helpers.client;

import io.finto.fineract.sdk.models.PostClientsAddressRequest;
import io.finto.fineract.sdk.models.PostClientsClientIdRequest;
import io.finto.fineract.sdk.models.PostClientsDatatable;
import io.finto.fineract.sdk.models.PostClientsDatatableData;
import io.finto.fineract.sdk.models.PostClientsRequest;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static io.finto.fineract.sdk.Constants.LOCALE;
import static io.finto.fineract.sdk.CustomDatatableNames.CUSTOMER_ADDITIONAL_FIELDS;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class TestClient {
    @NonNull
    @ToString.Exclude
    private final TestClientRepository<?> repository;
    @NonNull
    private final AtomicBoolean issued = new AtomicBoolean(false);
    // Do not mark the following properties as @NonNull you may want to simulate sending a bad request
    private String dateFormat;
    private Integer officeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer legalFormId;
    private ClientStatus status;
    private String locale;
    private String mobileNo;
    private String passportNumber;
    private String nationId;
    private String driverId;
    private List<TestClientAddress> address;

    public PostClientsRequest toClientRequest() {
        return PostClientsRequest.builder()
                .dateFormat(dateFormat)
                .firstname(firstName)
                .lastname(lastName)
                .officeId(officeId)
                .legalFormId(legalFormId)
                .locale(locale)
                .mobileNo(mobileNo)
                .address(toClientAddressRequest())
                .datatables(List.of(PostClientsDatatable.builder()
                        .registeredTableName(CUSTOMER_ADDITIONAL_FIELDS)
                        .data(PostClientsDatatableData.builder().locale(LOCALE).build())
                        .build()))
                .build();
    }

    private List<PostClientsAddressRequest> toClientAddressRequest() {
        return address == null ? null : address.stream().map(this::toClientAddress).collect(Collectors.toList());
    }

    private PostClientsAddressRequest toClientAddress(TestClientAddress testClientAddress) {
        return PostClientsAddressRequest.builder()
                .addressTypeId(testClientAddress.getAddressTypeId())
                .addressLine1(testClientAddress.getAddressLine1())
                .addressLine2(testClientAddress.getAddressLine2())
                .addressLine3(testClientAddress.getAddressLine3())
                .city(testClientAddress.getCity())
                .countryId(testClientAddress.getCountryId())
                .postalCode(testClientAddress.getPostalCode())
                .isActive(testClientAddress.getIsActive())
                .build();
    }

    public PostClientsClientIdRequest toStatusRequest() {
        var formatter = DateTimeFormatter.ofPattern(dateFormat);
        switch (status) {
            case CLOSED:
                return PostClientsClientIdRequest.builder()
                        .dateFormat(dateFormat)
                        .closureDate(ZonedDateTime.now(ZoneOffset.UTC).format(formatter))
                        .closureReasonId(22)
                        .locale("en")
                        .build();
            case ACTIVATED:
                return PostClientsClientIdRequest.builder()
                        .dateFormat(dateFormat)
                        .locale("en")
                        .activationDate(ZonedDateTime.now(ZoneOffset.UTC).format(formatter))
                        .build();
            case REACTIVATED:
                return PostClientsClientIdRequest.builder()
                        .dateFormat(dateFormat)
                        .locale("en")
                        .reactivationDate(ZonedDateTime.now(ZoneOffset.UTC).format(formatter))
                        .build();
        }
        throw new UnsupportedOperationException();
    }

    public TestClient markIssued() {
        issued.set(true);
        return this;
    }
}
