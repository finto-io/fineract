package io.finto.integration.fineract.test.helpers.client;

import io.finto.fineract.sdk.models.PostClientsClientIdRequest;
import io.finto.fineract.sdk.models.PostClientsRequest;
import lombok.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public PostClientsRequest toClientRequest() {
        return PostClientsRequest.builder()
                .dateFormat(dateFormat)
                .firstname(firstName)
                .lastname(lastName)
                .officeId(officeId)
                .legalFormId(legalFormId)
                .locale(locale)
                .build();
    }

    public PostClientsClientIdRequest toStatusRequest(){
        var formatter = DateTimeFormatter.ofPattern(dateFormat);
        switch (status){
            case CLOSED:
            case APPROVED:
                return PostClientsClientIdRequest.builder()
                        .dateFormat(dateFormat)
                        .locale("en")
                        .build();
            case ACTIVATED:
                return PostClientsClientIdRequest.builder()
                        .dateFormat(dateFormat)
                        .locale("en")
                        .activationDate(ZonedDateTime.now(ZoneOffset.UTC).format(formatter))
                        .build();
        }
        throw new UnsupportedOperationException();
    }

    public TestClient markIssued() {
        issued.set(true);
        return this;
    }
}
