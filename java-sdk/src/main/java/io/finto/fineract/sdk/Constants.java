package io.finto.fineract.sdk;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String USER = "mifos";
    public static final String LOCALE = "en";
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final Long INTERNAL_TRANSFER_PAYMENT_TYPE_ID = 4L;
    public static final Long GENDER_DICTIONARY_ID = 4L;
    public static final Long COUNTRY_DICTIONARY_ID = 28L;
    public static final Long DOCUMENT_TYPE_DICTIONARY_ID = 1L;
    public static final Long ADDRESS_TYPE_DICTIONARY_ID = 29L;
    public static final Long CLIENT_CLOSURE_REASON_DICTIONARY_ID = 14L;
    public static final String NATION_ID_CODE_NAME = "Id";
    public static final String PASSPORT_CODE_NAME = "Passport";
    public static final String DRIVER_ID_CODE_NAME = "Drivers License";
    public static final String RESIDENCE_ADDRESS_CODE_NAME = "Residence Address";
    public static final String WORK_ADDRESS_CODE_NAME = "Work Address";
    public static final String CUSTOMER_REQUEST_CODE_NAME = "Customer Request";
}
