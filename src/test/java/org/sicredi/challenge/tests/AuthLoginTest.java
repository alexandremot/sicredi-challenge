package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sicredi.challenge.model.LoginRequest;
import org.sicredi.challenge.model.LoginResponse;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Autenticação - POST /auth/login")
public class AuthLoginTest extends BaseTest {


    @Test
    @Description("Cenário 1: Autenticar com credenciais válidas com sucesso")
    void deveAutenticarComCredenciaisValidas() {
        var request = TestDataBuilder.loginValido();
        
        LoginResponse response = ApiHelper.enviar(
                requestSpec,
                "/auth/login",
                request,
                200,
                LoginResponse.class
        );

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername()).isNotNull();
    }

    @Test
    @Description("Cenário 2: Validar o tipo do conteúdo retornado na resposta")
    void deveRetornarContentTypeJson() {
        var response = RestAssured
                .given()
                    .spec(requestSpec)
                    .contentType("application/json")
                    .body(TestDataBuilder.loginValido())
                .when()
                    .post("/auth/login")
                .then()
                    .extract()
                    .response();

        assertThat(response.getContentType()).contains("application/json");
    }


    @Test
    @Description("Cenário 3: Validar que accessToken e refreshToken são retornados")
    void deveRetornarAccessTokenERefreshToken() {
        LoginResponse response = ApiHelper.enviar(
                requestSpec,
                "/auth/login",
                TestDataBuilder.loginValido(),
                200,
                LoginResponse.class
        );

        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
    }

    @Test
    @Description("Cenário 4a: AccessToken não deve ser nulo ou vazio e deve ser string (JWT)")
    void acessTokenDeveSerNaoNuloNaoVazioEString() {
        LoginResponse response = ApiHelper.enviar(
                requestSpec,
                "/auth/login",
                TestDataBuilder.loginValido(),
                200,
                LoginResponse.class
        );

        assertThat(response.getAccessToken())
                .isNotNull()
                .isNotEmpty()
                .isInstanceOf(String.class)
                .matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");
    }

    @Test
    @Description("Cenário 4b: RefreshToken não deve ser nulo ou vazio e deve ser string (JWT)")
    void refreshTokenDeveSerNaoNuloNaoVazioEString() {
        LoginResponse response = ApiHelper.enviar(
                requestSpec,
                "/auth/login",
                TestDataBuilder.loginValido(),
                200,
                LoginResponse.class
        );

        assertThat(response.getRefreshToken())
                .isNotNull()
                .isNotEmpty()
                .isInstanceOf(String.class)
                .matches("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");
    }


    @Test
    @Description("Cenário 5: Validar que os dados do usuário autenticado são retornados")
    void deveDevolverDadosComplertosDoUsuario() {
        LoginResponse response = ApiHelper.enviar(
                requestSpec,
                "/auth/login",
                TestDataBuilder.loginValido(),
                200,
                LoginResponse.class
        );

        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername()).isNotNull();
        assertThat(response.getEmail()).isNotNull();
        assertThat(response.getFirstName()).isNotNull();
        assertThat(response.getLastName()).isNotNull();
    }

    @Test
    @Description("Cenário 6: O username retornado deve corresponder ao username enviado")
    void usernameRetornadoDeveCorresponderAoEnviado() {
        var request = TestDataBuilder.loginValido();
        
        LoginResponse response = ApiHelper.enviar(
                requestSpec,
                "/auth/login",
                request,
                200,
                LoginResponse.class
        );

        assertThat(response.getUsername()).isEqualTo(request.getUsername());
    }


    @Test
    @Description("Cenário 7: Credenciais inválidas devem retornar 400")
    void deveRetornar400ComCredenciaisInvalidas() {
        var request = TestDataBuilder.loginComPasswordInvalido();

        var response = RestAssured
                .given()
                    .spec(requestSpec)
                    .contentType("application/json")
                    .body(request)
                .when()
                    .post("/auth/login")
                .then()
                    .extract()
                    .response();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertMensagemDeErro(response);
    }

    @ParameterizedTest(name = "Cenário 8: Campo {0} vazio ou ausente")
    @ValueSource(strings = {"username_vazio", "username_ausente", "password_vazio", "password_ausente"})
    @Description("Cenário 8: Campos obrigatórios ausentes ou vazios devem retornar 400")
    void deveRetornar400ComCamposObrigatoriosInvalidos(String cenario) {
        LoginRequest request = switch(cenario) {
            case "username_vazio" -> TestDataBuilder.loginComUsernameVazio();
            case "username_ausente" -> TestDataBuilder.loginSemUsername();
            case "password_vazio" -> TestDataBuilder.loginComPasswordVazio();
            case "password_ausente" -> TestDataBuilder.loginSemPassword();
            default -> TestDataBuilder.loginValido();
        };

        var response = RestAssured
                .given()
                    .spec(requestSpec)
                    .contentType("application/json")
                    .body(request)
                .when()
                    .post("/auth/login")
                .then()
                    .extract()
                    .response();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertMensagemDeErro(response);
    }

    @Test
    @Description("Cenário 9: Body não JSON em /auth/login deve retornar 4xx com mensagem")
    void deveRetornar4xxComBodyNaoJson() {
        Response response = RestAssured
                .given()
                    .spec(requestSpec)
                    .contentType("text/plain")
                    .body("texto_invalido")
                .when()
                    .post("/auth/login")
                .then()
                    .extract()
                    .response();

        assertThat(response.getStatusCode())
                .as("Body não JSON deve retornar erro de cliente (4xx)")
                .isBetween(400, 499);
        assertMensagemDeErro(response);
    }

    private void assertMensagemDeErro(Response response) {
        String message = response.jsonPath().getString("message");
        String error = response.jsonPath().getString("error");

        assertThat((message == null ? "" : message) + (error == null ? "" : error))
                .as("A resposta de erro deve conter 'message' ou 'error'")
                .isNotBlank();
    }
}




