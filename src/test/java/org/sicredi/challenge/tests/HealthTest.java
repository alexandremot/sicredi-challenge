package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@Feature("Health Check")
public class HealthTest extends BaseTest {

    @Test
    @Description("Cenário 1: Retornar status ok na API de teste")
    void deveRetornarStatusOkNaApiDeTest() {
        given()
                .spec(requestSpec)
                .when()
                .get("/test")
                .then()
                .statusCode(200)
                .body("status", equalTo("ok"))
                .body("method", equalTo("GET"));
    }
}