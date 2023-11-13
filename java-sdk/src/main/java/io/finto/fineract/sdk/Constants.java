package io.finto.fineract.sdk;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Constants {
    public static final String USER = "mifos";
    public static final String LOCALE = "en";
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    public static final String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN);
    public static final Long INTERNAL_TRANSFER_PAYMENT_TYPE_ID = 4L;
    public static final Long GENDER_DICTIONARY_ID = 4L;
    public static final Long COUNTRY_DICTIONARY_ID = 28L;
    public static final Long DOCUMENT_TYPE_DICTIONARY_ID = 1L;
    public static final Long ADDRESS_TYPE_DICTIONARY_ID = 29L;
    public static final Long CLIENT_CLOSURE_REASON_DICTIONARY_ID = 14L;
    public static final String RESIDENCE_ADDRESS_CODE_NAME = "Residence Address";
    public static final String WORK_ADDRESS_CODE_NAME = "Work Address";
    public static final String CUSTOMER_REQUEST_CODE_NAME = "Customer Request";
    public static final Long FUND_SOURCE_ID = 2L;
    public static final Long LOAN_PORTFOLIO_ID = 6L;
    public static final Long FEES_RECEIVABLE_ID = 7L;
    public static final Long INTEREST_RECEIVABLE_ID = 3L;
    public static final Long PENALTIES_RECEIVABLE_ID = 4L;
    public static final Long TRANSFER_IN_SUSPENSE_ID = 5L;
    public static final Long INCOME_FROM_INTEREST_ID = 8L;
    public static final Long INCOME_FROM_FEES_ID = 10L;
    public static final Long INCOME_FROM_PENALTIES_ID = 9L;
    public static final Long LOSSES_WRITTEN_OFF_ID = 11L;
    public static final Long OVER_PAYMENT_LIABILITY_ID = 12L;
    public static final String DATE_TIME_FORMAT_WITHOUT_SEC_PATTERN = "dd MMMM yyyy HH:mm";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_WITHOUT_SEC_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_WITHOUT_SEC_PATTERN, new Locale("en"));
    public static final Integer CHARGE_TIME_TYPE_FEES_ID = 1;
    public static final Integer CHARGE_TIME_TYPE_LATE_PAYMENT_ID = 9;
    public static final Integer CHARGE_TIME_TYPE_EARLY_SETTLEMENT_ID = 2;
    public static final Integer CHARGE_CALCULATION_TYPE_FIXED_ID = 1;
    public static final Integer CHARGE_CALCULATION_TYPE_PERCENTAGE_ID = 2;
    public static final Integer INTEREST_TYPE_FIXED_ID = 1;
    public static final Integer INTEREST_TYPE_REDUCING_ID = 0;
    public static final String CALCULATE_LOAN_SCHEDULE = "calculateLoanSchedule";
    public static final String SCHEDULE_DATE_FORMAT_PATTERN = "dd MMMM yyyy";
    public static final DateTimeFormatter SCHEDULE_DATE_FORMATTER = DateTimeFormatter.ofPattern(SCHEDULE_DATE_FORMAT_PATTERN, new Locale("en"));
    public static final String LOAN_PRODUCT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm";
    public static final DateTimeFormatter LOAN_PRODUCT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(LOAN_PRODUCT_DATE_TIME_FORMAT, new Locale("en"));
    public static final String INDIVIDUAL = "individual";
    public static final String REPAYMENT = "repayment";
    public static final String FORECLOSURE = "foreclosure";
    public static final String PREPAY_LOAN = "prepayLoan";
}
