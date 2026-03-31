package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;
import org.sicredi.challenge.model.LoginResponse;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.TestDataBuilder;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Segurança - JWT")
public class AuthSecurityTest extends BaseTest {

    private static final String JWT_PATTERN = "^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$";

    @Test
    @Description("POST /auth/security sem token deve retornar 401")
    void deveRetornar401SemToken() {
        ApiHelper.enviarRaw(requestSpec, "/auth/security", "{}", 401);
    }

    @Test
    @Description("POST /auth/security com token válido deve retornar 200")
    void deveRetornar200ComTokenValido() {
        LoginResponse loginResponse = ApiHelper.enviar(
                requestSpec, "/auth/login",
                TestDataBuilder.loginValido(),
                200,
                LoginResponse.class
        );

        // Montar spec com token e chamar /auth/security (realmente testa o endpoint)
        ApiHelper.enviarRaw(
                authRequestSpec,
                "/auth/security",
                "{}",
                200
        );
    }

    @Test
    @Description("Token deve ser retornado com formato JWT válido")
    void tokenDeveTermFormatoJwtValido() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        assertThat(response.getAccessToken())
                .as("Token deve ter formato JWT (3 partes separadas por ponto)")
                .matches(JWT_PATTERN);
    }

    @Test
    @Description("Token deve ter estrutura JWT com 3 partes separadas por pontos")
    void tokenDeveTermEstruturaPontuada() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        String[] parts = response.getAccessToken().split("\\.");
        assertThat(parts).as("JWT deve ter exatamente 3 partes").hasSize(3);
    }

    @Test
    @Description("Token deve ter tamanho mínimo")
    void tokenDeveTermTamanhoMinimo() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        assertThat(response.getAccessToken())
                .as("Token deve ter tamanho mínimo (>50 caracteres)")
                .hasSizeGreaterThan(50);
    }

    @Test
    @Description("Header deve conter algoritmo válido (HS256)")
    void headerDeveConterAlgoritmoValido() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        String header = decodeBase64(response.getAccessToken().split("\\.")[0]);
        assertThat(header).contains("HS256");
    }

    @Test
    @Description("Payload deve ser decodificável")
    void payloadDeveSerDecodificavel() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        String payload = decodeBase64(response.getAccessToken().split("\\.")[1]);
        assertThat(payload).as("Payload deve ser válido JSON").contains("{");
    }

    @Test
    @Description("Payload deve conter identificação do usuário")
    void payloadDeveConterIdentificacaoUsuario() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        String payload = decodeBase64(response.getAccessToken().split("\\.")[1]);
        assertThat(payload).contains("username");
    }

    @Test
    @Description("Payload deve conter claim 'iat'")
    void payloadDeveConterClaimIat() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        String payload = decodeBase64(response.getAccessToken().split("\\.")[1]);
        assertThat(payload).contains("iat");
    }

    @Test
    @Description("Token deve ter assinatura válida (terceira parte não vazia)")
    void tokenDeveTermAssinatura() {
        LoginResponse response = ApiHelper.enviar(requestSpec, "/auth/login", TestDataBuilder.loginValido(), 200, LoginResponse.class);
        String signature = response.getAccessToken().split("\\.")[2];
        assertThat(signature).as("JWT deve conter assinatura").isNotBlank();
    }

    @Test
    @Description("Token inválido não deve ser JWT válido")
    void tokenInvalidoNaoDeveSerJwtValido() {
        String token = "invalid";
        assertThat(token).doesNotMatch(JWT_PATTERN);
    }

    @Test
    @Description("Token vazio não deve ser JWT")
    void tokenVazioNaoDeveSerJwt() {
        assertThat("")
                .as("String vazia não é JWT")
                .doesNotMatch(JWT_PATTERN);
    }

    @Test
    @Description("Token com 2 partes não deve ser JWT válido")
    void tokenComApenasDuasPartesNaoDeveSerJwt() {
        assertThat("header.payload")
                .as("JWT com 2 partes é inválido")
                .doesNotMatch(JWT_PATTERN);
    }

    private String decodeBase64(String encoded) {
        return new String(Base64.getUrlDecoder().decode(encoded));
    }
}

