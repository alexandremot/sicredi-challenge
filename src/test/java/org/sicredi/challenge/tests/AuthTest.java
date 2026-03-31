package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.Test;
import org.sicredi.challenge.model.LoginResponse;
import org.sicredi.challenge.model.ProductResponse;
import org.sicredi.challenge.annotations.BugFound;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Autenticação")
public class AuthTest extends BaseTest {

    @Test
    @Description("Cenário 1: Deve acessar /auth/products com token obtido no login")
    void deveBuscarProdutosComTokenGeradoNoLogin() {
        LoginResponse response = ApiHelper.enviar(
                requestSpec,
                "/auth/login",
                TestDataBuilder.loginValido(),
                200,
                LoginResponse.class
        );

        var authenticatedSpec = new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + response.getAccessToken())
                .build();

        ProductResponse products = ApiHelper.obter(
                authenticatedSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        assertThat(products.getProducts()).isNotEmpty();
        assertThat(products.getTotal()).isPositive();
    }

    @Test
    @Description("Cenário 2: Deve rejeitar token inválido em /auth/products")
    void deveRetornar401ComTokenInvalido() {
        var invalidTokenSpec = new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer token-invalido")
                .build();

        ApiHelper.obterRaw(invalidTokenSpec, "/auth/products", 401);
    }

    @Test
    @BugFound("BUG #1: /auth/products sem token retorna 200, mas contrato exige 401")
    @Description("Cenário 3: /auth/products sem token deve retornar 401")
    void deveRetornar401SemToken() {
        ApiHelper.obterRaw(requestSpec, "/auth/products", 401);
    }
}
