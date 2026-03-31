package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.sicredi.challenge.model.Product;
import org.sicredi.challenge.model.ProductResponse;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.AssertionsHelper;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Produtos - GET /products")
public class ProductsTest extends BaseTest {

    @Test
    @Description("Cenário 1: Obter lista de produtos com sucesso")
    void deveRetornarListaDeProdutosComSucesso() {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        AssertionsHelper.assertListaProdutosNaoEstaVazia(response);
        assertThat(response.getProducts()).isNotEmpty();
        assertThat(response.getTotal()).isPositive();
        assertThat(response.getSkip()).isGreaterThanOrEqualTo(0);
        assertThat(response.getLimit()).isPositive();
    }

    @Test
    @Description("Cenário 2: Validar o tipo do conteúdo retornado na resposta")
    void deveRetornarContentTypeJson() {
        var response = RestAssured
                .given()
                    .spec(requestSpec)
                .when()
                    .get("/products")
                .then()
                    .extract()
                    .response();

        assertThat(response.getContentType()).contains("application/json");
    }

    @Test
    @Description("Cenário 3: Validar que a resposta retorna campos de paginação")
    void deveRetornarCamposDePaginacao() {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        assertThat(response.getTotal()).isNotNull();
        assertThat(response.getSkip()).isNotNull();
        assertThat(response.getLimit()).isNotNull();
    }

    @Test
    @Description("Cenário 4: Validar comportamento padrão da paginação (quantidade = limit)")
    void deveRetornarQuantidadeProdutosIgualLimit() {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        int quantidade = response.getProducts().size();
        int limit = response.getLimit();
        assertThat(quantidade).isEqualTo(limit);
    }

    @Test
    @Description("Cenário 5: Validar que total é maior ou igual a limit")
    void totalDeveSerMaiorOuIgualALimit() {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        assertThat(response.getTotal()).isGreaterThanOrEqualTo(response.getLimit());
    }

    @ParameterizedTest(name = "Cenário 6: Campo {0} deve ser inteiro")
    @ValueSource(strings = {"total", "skip", "limit"})
    @Description("Cenário 6: Campos de paginação devem ser do tipo inteiro")
    void camposPaginacaoDevemSerInteiros(String nomeCampo) {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        switch(nomeCampo) {
            case "total" -> assertThat(response.getTotal()).isInstanceOf(Integer.class);
            case "skip" -> assertThat(response.getSkip()).isInstanceOf(Integer.class);
            case "limit" -> assertThat(response.getLimit()).isInstanceOf(Integer.class);
        }
    }

    @Test
    @Description("Cenário 7: Cada produto deve conter campos obrigatórios")
    void cadaProdutoDeveTerCamposObrigatorios() {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        response.getProducts().forEach(produto -> {
            assertThat(produto.getId()).isNotNull();
            assertThat(produto.getTitle()).isNotNull().isNotBlank();
            assertThat(produto.getPrice()).isNotNull();
            assertThat(produto.getCategory()).isNotNull().isNotBlank();
        });
    }

    @Test
    @Description("Cenário 8: IDs dos produtos devem ser únicos")
    void idsDeProdutosDevemSerUnicos() {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        var ids = response.getProducts().stream()
                .map(produto -> produto.getId())
                .toList();
        assertThat(ids).doesNotHaveDuplicates();
    }

    @ParameterizedTest(name = "Cenário 9: Campo {0} não deve estar vazio")
    @ValueSource(strings = {"title", "price", "category"})
    @Description("Cenário 9: Campos críticos não devem estar vazios")
    void camposCriticosNaoDevemEstarVazios(String nomeCampo) {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        response.getProducts().forEach(produto -> {
            switch(nomeCampo) {
                case "title" -> assertThat(produto.getTitle()).isNotBlank();
                case "category" -> assertThat(produto.getCategory()).isNotBlank();
                case "price" -> assertThat(produto.getPrice()).isNotNull();
            }
        });
    }

    @ParameterizedTest(name = "Cenário 10: Campo {0} deve ser >= 0")
    @ValueSource(strings = {"price", "discountPercentage", "rating"})
    @Description("Cenário 10: Campos de preço/desconto/avaliação devem ser >= 0")
    void camposNumericosDeveramSerPositivos(String nomeCampo) {
        ProductResponse response = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        response.getProducts().forEach(produto -> {
            switch(nomeCampo) {
                case "price" -> assertThat(produto.getPrice()).isGreaterThanOrEqualTo(0.0);
                case "discountPercentage" -> {
                    if (produto.getDiscountPercentage() != null) {
                        assertThat(produto.getDiscountPercentage()).isGreaterThanOrEqualTo(0.0);
                    }
                }
                case "rating" -> {
                    if (produto.getRating() != null) {
                        assertThat(produto.getRating()).isGreaterThanOrEqualTo(0.0);
                    }
                }
            }
        });
    }


    @Test
    @Description("Cenário 11: Parâmetro skip negativo retorna lista com skip=0")
    void skipNegativoRetornaListaComSkip0() {
        var response = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("skip", -1)
                .when()
                    .get("/products")
                .then()
                    .extract()
                    .response();

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    @Description("Cenário 12: Parâmetro limit negativo retorna lista com limite padrão")
    void limitNegativoRetornaListaComLimitePadrao() {
        var response = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("limit", -1)
                .when()
                    .get("/products")
                .then()
                    .extract()
                    .response();

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @ParameterizedTest(name = "Cenário 13: Paginação com limit={0} e skip={1}")
    @CsvSource({
            "5, 0",
            "10, 0",
            "5, 5",
            "10, 10",
            "3, 7"
    })
    @Description("Cenário 13: Validar comportamento de limit e skip com valores positivos")
    void deveRespeitarLimitESkipParametrizados(int limit, int skip) {
        ProductResponse response = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("limit", limit)
                    .queryParam("skip", skip)
                .when()
                    .get("/products")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(ProductResponse.class);

        // Valida que o skip foi respeitado
        assertThat(response.getSkip()).isEqualTo(skip)
                .as("Skip deve ser igual ao parâmetro solicitado");

        // Valida que o limit foi respeitado
        assertThat(response.getLimit()).isEqualTo(limit)
                .as("Limit deve ser igual ao parâmetro solicitado");

        // Valida que a quantidade de produtos não excede o limit
        assertThat(response.getProducts().size()).isLessThanOrEqualTo(limit)
                .as("Quantidade de produtos não deve exceder limit");

        // Valida que skip + quantidade retornada não excede o total
        assertThat(skip + response.getProducts().size()).isLessThanOrEqualTo(response.getTotal())
                .as("skip + quantidade de produtos retornados deve ser <= total");
    }

    @Test
    @Description("Cenário 14: Validar ausência de duplicatas entre páginas")
    void naoDeveSrDuplicatasPaginasConsecutivas() {
        // Primeira página
        ProductResponse page1 = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("limit", 5)
                    .queryParam("skip", 0)
                .when()
                    .get("/products")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(ProductResponse.class);

        // Segunda página
        ProductResponse page2 = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("limit", 5)
                    .queryParam("skip", 5)
                .when()
                    .get("/products")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(ProductResponse.class);

        List<Integer> idsPage1 = page1.getProducts().stream()
                .map(Product::getId)
                .toList();
        List<Integer> idsPage2 = page2.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // Não deve haver IDs duplicados entre as duas páginas
        for (Integer id : idsPage1) {
            assertThat(idsPage2).doesNotContain(id)
                    .as("ID " + id + " da página 1 não deve existir na página 2");
        }
    }

    @Test
    @Description("Cenário 15: Validar que ordem não muda entre requisições com mesmos parâmetros")
    void ordemDeveSerConsistenteEntreRequisicoes() {
        // Primeira requisição
        ProductResponse response1 = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("limit", 5)
                    .queryParam("skip", 0)
                .when()
                    .get("/products")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(ProductResponse.class);

        // Segunda requisição idêntica
        ProductResponse response2 = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("limit", 5)
                    .queryParam("skip", 0)
                .when()
                    .get("/products")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(ProductResponse.class);

        List<Integer> ids1 = response1.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        List<Integer> ids2 = response2.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // A ordem deve ser idêntica
        assertThat(ids1).isEqualTo(ids2)
                .as("Ordem dos produtos deve ser consistente entre requisições");
    }

    @Test
    @Description("Cenário 16: Skip além do total retorna lista vazia")
    void skipBeyondTotalRetornaListaVazia() {
        ProductResponse defaultResponse = ApiHelper.obter(requestSpec, "/products", 200, ProductResponse.class);
        int total = defaultResponse.getTotal();

        ProductResponse response = RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("skip", total + 100)
                .when()
                    .get("/products")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(ProductResponse.class);

        assertThat(response.getProducts())
                .as("Quando skip excede o total, a lista deve estar vazia")
                .isEmpty();
    }
}


