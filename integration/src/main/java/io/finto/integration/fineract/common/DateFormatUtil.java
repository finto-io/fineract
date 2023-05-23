package io.finto.integration.fineract.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtil {
    public static final String dateFormat = "yyyy-MM-dd";

    public static String convertDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
