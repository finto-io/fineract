package io.finto.integration.fineract.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

    public class FineractBusinessErrorHandler {

    private static final String CLIENT_ALREADY_EXISTS_PATTER = "Client (.+) a .* with unique key .*";

    public String convertMessage(String errorMessage) {
        if (errorMessage.matches(CLIENT_ALREADY_EXISTS_PATTER)){
            Pattern pattern = Pattern.compile(CLIENT_ALREADY_EXISTS_PATTER);
            Matcher matcher = pattern.matcher(errorMessage);
            if (matcher.find()){
                return errorMessage.replace(matcher.group(1), "already exists with");
            }
        }
        return errorMessage;
    }

}
