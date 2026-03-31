package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;
import org.sicredi.challenge.model.Product;
import org.sicredi.challenge.model.ProductResponse;
import org.sicredi.challenge.annotations.BugFound;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.AssertionsHelper;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Produtos Autenticados - GET /auth/products")
public class GetAuthProductsTest extends BaseTest {


    @Test
    @Description("Cenário 1: Obter lista de produtos com token válido (status 200)")
    void deveRetornarListaDeProdutosAutenticado() {
        ProductResponse response = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        AssertionsHelper.assertListaProdutosNaoEstaVazia(response);
        assertThat(response.getProducts()).isNotEmpty();
    }

    @Test
    @Description("Cenário 2: Validar o tipo do conteúdo retornado na resposta")
    void deveRetornarContentTypeJson() {
        var response = io.restassured.RestAssured
                .given()
                    .spec(authRequestSpec)
                .when()
                    .get("/auth/products")
                .then()
                    .extract()
                    .response();

        assertThat(response.getContentType()).contains("application/json");
    }


    @Test
    @Description("Cenário 3: Cada produto deve conter campos obrigatórios")
    void deveRetornarCamposObrigatoriosDoProduto() {
        ProductResponse response = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        response.getProducts().forEach(produto -> {
            AssertionsHelper.assertProdutoComIdTitlePriceECategoryValidos(produto);
            assertThat(produto.getId()).isPositive();
            assertThat(produto.getTitle()).isNotBlank();
            assertThat(produto.getPrice()).isNotNegative();
            assertThat(produto.getCategory()).isNotBlank();
        });
    }

    @Test
    @Description("Cenário 4: Campos críticos não devem estar vazios")
    void deveRetornarCamposCriticosNaoVazios() {
        ProductResponse response = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        response.getProducts().forEach(AssertionsHelper::assertProdutoComTitleCategoryEDescricaoNaoVazios);
    }

    @Test
    @Description("Cenário 5: Produtos devem conter metadados completos (dimensões, warranty, shipping)")
    void deveRetornarMetadadosCompletos() {
        ProductResponse response = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        response.getProducts().forEach(produto -> {
            AssertionsHelper.assertProdutoTemInformacoesDeEnvioGarantiaEDisponibilidade(produto);
            AssertionsHelper.assertDimensoesDoProdutoSaoValidas(produto);
        });
    }


    @Test
    @Description("Cenário 6: IDs dos produtos devem ser únicos")
    void deveRetornarIdsDosProduosUnicos() {
        ProductResponse response = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        var ids = response.getProducts().stream()
                .map(Product::getId)
                .distinct()
                .count();

        assertThat(ids)
                .as("Todos os IDs devem ser únicos")
                .isEqualTo(response.getProducts().size());
    }

    @Test
    @Description("Cenário 7: Campos numéricos devem estar em ranges válidos (rating, discount, stock)")
    void deveRetornarCamposNumericosValidos() {
        ProductResponse response = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        response.getProducts().forEach(produto -> {
            if (produto.getRating() != null) {
                AssertionsHelper.assertRatingEstaDentroDoIntervaloValido(produto.getRating());
            }

            if (produto.getDiscountPercentage() != null) {
                assertThat(produto.getDiscountPercentage())
                        .as("Desconto deve estar entre 0-100")
                        .isBetween(0.0, 100.0);
            }

            if (produto.getStock() != null) {
                AssertionsHelper.assertStockEhValidoEMaiorOuIgualAZero(produto.getStock());
            }
        });
    }

    @Test
    @Description("Cenário 8: Reviews devem ter estrutura correta com ratings válidos")
    void deveRetornarReviewsComEstruturValida() {
        ProductResponse response = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        response.getProducts().forEach(produto -> {
            if (produto.getReviews() != null && !produto.getReviews().isEmpty()) {
                AssertionsHelper.assertProdutoTemReviewsComEstruturasValidas(produto);
            }
        });
    }


    @Test
    @BugFound("BUG #1: /auth/products sem token retorna 200, mas contrato exige 401")
    @Description("Cenário 9: Requisição sem token deve retornar 401 com mensagem de erro")
    void deveRetornar401AoBuscarProdutosSemToken() {
        var response = ApiHelper.obterRaw(requestSpec, "/auth/products", 401);

        String message = response.jsonPath().getString("message");
        String error = response.jsonPath().getString("error");

        assertThat((message == null ? "" : message) + (error == null ? "" : error))
                .as("A resposta 401 deve conter mensagem de erro")
                .isNotBlank();
    }

    @Test
    @Description("Cenário 11: Comparação com GET /products (sem autenticação)")
    void deveComparaComGetProductsPublico() {
        ProductResponse publicResponse = ApiHelper.obter(
                requestSpec,
                "/products",
                200,
                ProductResponse.class
        );

        ProductResponse authResponse = ApiHelper.obter(
                authRequestSpec,
                "/auth/products",
                200,
                ProductResponse.class
        );

        assertThat(publicResponse.getProducts()).isNotEmpty();
        assertThat(authResponse.getProducts()).isNotEmpty();

        publicResponse.getProducts().forEach(AssertionsHelper::assertProdutoComIdTitlePriceECategoryValidos);

        authResponse.getProducts().forEach(AssertionsHelper::assertProdutoComIdTitlePriceECategoryValidos);
    }
}

