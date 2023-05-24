package io.finto.integration.fineract.test.helpers;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.text.MatchesPattern;

public class RestHelper {

    public RestHelper() {
    }

    public Matcher<String> isRequestId() {
        return MatchesPattern.matchesPattern("^[0-9a-fA-F]{16,32}$");
    }

    public Matcher<String> isDateTime() {
        return MatchesPattern.matchesPattern("^2\\d{3}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d$");
    }

    public ResponseSpecification responseError(int errorCode, String statusLine, String errorMessage) {
        return (new ResponseSpecBuilder())
                .expectContentType(ContentType.JSON)
                .expectStatusCode(errorCode / 1000)
                .expectStatusLine(statusLine)
                .expectBody("Code", CoreMatchers.equalTo(String.valueOf(errorCode)))
                .expectBody("Message", CoreMatchers.equalTo(errorMessage))
                .expectBody("RequestId", this.isRequestId())
                .expectBody("DateTime", this.isDateTime()).build();
    }

    public ResponseSpecification responseError(int errorCode, String errorMessage) {
        return this.responseError(errorCode, errorCode / 1000 + " " + errorMessage, errorMessage);
    }

    public ResponseSpecification response400ValidationFailed(String errorMessage) {
        return (new ResponseSpecBuilder())
                .expectContentType(ContentType.JSON)
                .expectStatusCode(400)
                .expectBody("Code", CoreMatchers.equalTo(String.valueOf(400001)))
                .expectBody("Message", CoreMatchers.equalTo(errorMessage))
                .expectBody("RequestId", this.isRequestId())
                .expectBody("DateTime", this.isDateTime()).build();
    }

}
