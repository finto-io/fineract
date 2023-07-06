package io.finto.integration.fineract.test.helpers.client;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.random;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestClientCreator<T extends TestClientRepository<T>> {

    private static final Random PRNG = new Random();
    private static final List<String> availableDateFormat = List.of("dd-MM-yyyy");
    private static final List<String> availableLocale = List.of("en");


    @NonNull
    protected final T repository;
    private String dateFormat;
    private Integer officeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private Integer legalFormId;
    private ClientStatus status;
    private String locale;
    private List<TestClientAddress> address;

    public TestClientCreator<T> withDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public TestClientCreator<T> withOfficeId(Integer officeId) {
        this.officeId = officeId;
        return this;
    }

    public TestClientCreator<T> withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public TestClientCreator<T> withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public TestClientCreator<T> withFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public TestClientCreator<T> withLegalFormId(Integer legalFormId) {
        this.legalFormId = legalFormId;
        return this;
    }

    public TestClientCreator<T> withStatus(ClientStatus status) {
        this.status = status;
        return this;
    }

    public TestClientCreator<T> withLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public TestClientCreator<T> withAddress(List<TestClientAddress> address) {
        this.address = address;
        return this;
    }

    public TestClientCreator<T> withRandomParams() {
        TestClientAddress address = TestClientAddress.builder()
                .addressTypeId(17L)
                .addressLine1("A")
                .addressLine2("B")
                .addressLine3("C")
                .city("Amman")
                .countryId(19)
                .postalCode(107490L)
                .isActive(true)
                .build();

        return withDateFormat(availableDateFormat.get(PRNG.nextInt(availableDateFormat.size())))
                .withOfficeId(1)
                .withFirstName(random(10, true, true))
                .withLastName(random(10, true, true))
                .withLegalFormId(1)
                .withLocale("en")
                .withAddress(List.of(address))
                .withStatus(ClientStatus.ACTIVATED);
    }

    public T create() {
        return repository.submitClient(TestClient.builder()
                .repository(repository)
                .dateFormat(dateFormat)
                .officeId(officeId)
                .firstName(firstName)
                .lastName(lastName)
                .legalFormId(legalFormId)
                .status(status)
                .locale(locale)
                .address(address)
                .build());
    }

    @SuppressWarnings("unchecked")
    public <R extends TestClientCreator<T>> R castTo(Class<R> to) {
        return (R) this;
    }

}
