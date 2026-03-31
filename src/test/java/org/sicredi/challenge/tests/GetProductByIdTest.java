package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sicredi.challenge.model.Product;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.AssertionsHelper;
import org.sicredi.challenge.utils.TestConstants;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Produtos - GET /products/{id}")
public class GetProductByIdTest extends BaseTest {


    @Test
    @Description("Cenário 1: Obter detalhes de um produto existente com sucesso")
    void deveRetornarProdutoPorIdValido() {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        AssertionsHelper.assertProdutoComIdEsperado(response, id);
    }

    @Test
    @Description("Cenário 2: Validar o tipo do conteúdo retornado na resposta")
    void deveRetornarContentTypeJson() {
        int id = TestConstants.randomValidProductId();
        
        var response = RestAssured
                .given()
                    .spec(requestSpec)
                .when()
                    .get("/products/" + id)
                .then()
                    .extract()
                    .response();

        assertThat(response.getContentType()).contains("application/json");
    }


    @Test
    @Description("Cenário 3: Produto deve conter todos os campos obrigatórios")
    void deveRetornarCamposObrigatoriosDoProduto() {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        AssertionsHelper.assertProdutoComIdTitlePriceECategoryValidos(response);
    }

    @Test
    @Description("Cenário 4: Campos críticos não devem estar vazios")
    void deveRetornarCamposCriticosNaoVazios() {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        AssertionsHelper.assertProdutoComTitleCategoryEDescricaoNaoVazios(response);
    }

    @ParameterizedTest(name = "Campo {0} deve ser válido (>= 0)")
    @ValueSource(strings = {"price", "discountPercentage", "stock"})
    @Description("Cenário 5: Campos numéricos devem ser válidos (parametrizado)")
    void deveValidarCamposNumericos(String campo) {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        switch (campo) {
            case "price" -> AssertionsHelper.assertCampoNumericoEhMaiorOuIgualAZero(response.getPrice(), "price");
            case "discountPercentage" -> AssertionsHelper.assertCampoNumericoEhMaiorOuIgualAZero(response.getDiscountPercentage(), "discountPercentage");
            case "stock" -> AssertionsHelper.assertStockEhValidoEMaiorOuIgualAZero(response.getStock());
        }
    }

    @Test
    @Description("Cenário 6: O ID retornado deve corresponder ao ID solicitado")
    void deveRetornarIdIgualAoSolicitado() {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        AssertionsHelper.assertProdutoComIdEsperado(response, id);
        assertThat(response.getId()).isEqualTo(id);
    }


    @Test
    @Description("Cenário 7: Produto deve conter metadados de envio e garantia")
    void deveRetornarMetadadosEnvioGarantia() {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        AssertionsHelper.assertProdutoTemInformacoesDeEnvioGarantiaEDisponibilidade(response);
    }

    @Test
    @Description("Cenário 8: Dimensões devem estar presentes e ser válidas")
    void deveRetornarDimensoesValidas() {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        AssertionsHelper.assertDimensoesDoProdutoSaoValidas(response);
    }

    @Test
    @Description("Cenário 9: Reviews devem estar presentes com estrutura válida")
    void deveRetornarReviewsComEstruturValida() {
        int id = TestConstants.randomValidProductId();

        Product response = ApiHelper.obter(
                requestSpec,
                "/products/" + id,
                200,
                Product.class
        );

        AssertionsHelper.assertProdutoTemReviewsComEstruturasValidas(response);
    }


    @Test
    @Description("Cenário 10: Requisição para produto inexistente deve retornar 404")
    void deveRetornar404ParaIdInexistente() {
        var response = ApiHelper.obterRaw(
                requestSpec,
                "/products/999999",
                404
        );

        String message = response.jsonPath().getString("message");
        String error = response.jsonPath().getString("error");

        assertThat((message == null ? "" : message) + (error == null ? "" : error))
                .as("Resposta 404 deve conter mensagem de erro")
                .isNotBlank();
    }

    @Test
    @Description("Cenário 11: Requisição com ID não numérico deve retornar erro")
    void deveRetornar404ParaIdNaoNumerico() {
        ApiHelper.obterRaw(
                requestSpec,
                "/products/abc",
                404
        );
    }

    @Test
    @Description("Cenário 12: Edge case - ID zero deve retornar 404")
    void deveRetornar404ParaIdZero() {
        ApiHelper.obterRaw(
                requestSpec,
                "/products/" + TestConstants.PRODUCT_ZERO_ID,
                404
        );
    }

    @Test
    @Description("Cenário 13: Edge case - ID negativo deve retornar 404")
    void deveRetornar404ParaIdNegativo() {
        ApiHelper.obterRaw(
                requestSpec,
                "/products/" + TestConstants.PRODUCT_NEGATIVE_ID,
                404
        );
    }
}