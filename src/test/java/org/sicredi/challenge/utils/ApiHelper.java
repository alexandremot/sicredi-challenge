package org.sicredi.challenge.utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Utilitário para requisições HTTP em testes de API.
 * 
 * Convenção de nomenclatura: Portuguese (Português)
 * - obter: GET request com desserialização automática
 * - enviar: POST request com desserialização automática
 * - obterRaw: GET request retornando Response bruta
 * - enviarRaw: POST request retornando Response bruta
 * 
 * A nomenclatura em português é consistente com o projeto
 * e diferencia de métodos de terceiros (rest-assured usa .get()/.post()).
 */
public class ApiHelper {

    /**
     * GET request com desserialização automática da resposta.
     * 
     * @param spec RequestSpecification com headers/auth
     * @param endpoint caminho do endpoint (ex: "/products")
     * @param expectedStatus status HTTP esperado
     * @param responseClass classe para desserialização
     * @return objeto desserializado da resposta
     */
    public static <T> T obter(RequestSpecification spec, String endpoint, int expectedStatus, Class<T> responseClass) {
        return given()
                .spec(spec)
                .when()
                .get(endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract()
                .as(responseClass);
    }

    /**
     * POST request com desserialização automática da resposta.
     * 
     * @param spec RequestSpecification com headers/auth
     * @param endpoint caminho do endpoint (ex: "/products/add")
     * @param body corpo da requisição
     * @param expectedStatus status HTTP esperado
     * @param responseClass classe para desserialização
     * @return objeto desserializado da resposta
     */
    public static <T> T enviar(RequestSpecification spec, String endpoint, Object body, int expectedStatus, Class<T> responseClass) {
        return given()
                .spec(spec)
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract()
                .as(responseClass);
    }

    /**
     * GET request retornando Response bruta (sem desserialização).
     * 
     * @param spec RequestSpecification com headers/auth
     * @param endpoint caminho do endpoint (ex: "/products")
     * @param expectedStatus status HTTP esperado
     * @return Response bruta para inspeção manual
     */
    public static Response obterRaw(RequestSpecification spec, String endpoint, int expectedStatus) {
        return given()
                .spec(spec)
                .when()
                .get(endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract()
                .response();
    }

    /**
     * POST request retornando Response bruta (sem desserialização).
     * 
     * @param spec RequestSpecification com headers/auth
     * @param endpoint caminho do endpoint (ex: "/products/add")
     * @param body corpo da requisição
     * @param expectedStatus status HTTP esperado
     * @return Response bruta para inspeção manual
     */
    public static Response enviarRaw(RequestSpecification spec, String endpoint, Object body, int expectedStatus) {
        return given()
                .spec(spec)
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract()
                .response();
    }
}


