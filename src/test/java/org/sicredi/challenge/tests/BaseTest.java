package org.sicredi.challenge.tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.sicredi.challenge.utils.TestConstants;
import org.sicredi.challenge.utils.TokenManager;

public abstract class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static RequestSpecification authRequestSpec;

    @BeforeAll
    static void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(TestConstants.BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();

        authRequestSpec = new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + TokenManager.getToken())
                .build();
    }
}