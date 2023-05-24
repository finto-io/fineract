package io.finto.fineract.sdk;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String LOCALE = "en";
    public static final String DATE_FORMAT_PATTERN = "dd MMMM yyyy";
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
}
