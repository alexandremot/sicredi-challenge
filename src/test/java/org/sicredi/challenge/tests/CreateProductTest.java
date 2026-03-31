package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sicredi.challenge.model.Product;
import org.sicredi.challenge.model.ProductRequest;
import org.sicredi.challenge.annotations.BugFound;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.AssertionsHelper;
import org.sicredi.challenge.utils.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;


@Feature("Produtos - POST /products/add")
public class CreateProductTest extends BaseTest {


    @Test
    @Description("Cenário 1: Criar um produto com sucesso (status 201)")
    void deveCriarProdutoComSucesso() {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        assertThat(response.getId())
                .as("ID do produto criado deve ser positivo")
                .isNotNull()
                .isPositive();
    }

    @Test
    @Description("Cenário 2: Validar o tipo do conteúdo retornado na resposta")
    void deveRetornarContentTypeJson() {
        ProductRequest request = TestDataBuilder.produtoValido();
        
        var response = RestAssured
                .given()
                    .spec(requestSpec)
                    .body(request)
                .when()
                    .post("/products/add")
                .then()
                    .extract()
                    .response();

        assertThat(response.getContentType()).contains("application/json");
    }


    @Test
    @Description("Cenário 3: Produto deve conter todos os campos obrigatórios na criação")
    void deveRetornarCamposObrigatoriosDoProduto() {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        AssertionsHelper.assertProdutoComIdTitlePriceECategoryValidos(response);
    }

    @Test
    @Description("Cenário 4: Campos críticos retornados não devem estar vazios")
    void deveRetornarCamposCriticosNaoVazios() {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        AssertionsHelper.assertProdutoComTitleCategoryEDescricaoNaoVazios(response);
    }

    @ParameterizedTest(name = "Campo {0} deve ser válido (>= 0)")
    @ValueSource(strings = {"price", "discountPercentage", "stock"})
    @Description("Cenário 5: Campos numéricos na resposta devem ser válidos (parametrizado)")
    void deveValidarCamposNumericos(String campo) {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        switch (campo) {
            case "price" -> AssertionsHelper.assertCampoNumericoEhMaiorOuIgualAZero(response.getPrice(), "price");
            case "discountPercentage" -> AssertionsHelper.assertCampoNumericoEhMaiorOuIgualAZero(response.getDiscountPercentage(), "discountPercentage");
            case "stock" -> AssertionsHelper.assertStockEhValidoEMaiorOuIgualAZero(response.getStock());
        }
    }

    @Test
    @Description("Cenário 6: Os dados enviados devem ser retornados no produto criado")
    void deveRetornarDadosEnviadosNaResposta() {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getPrice()).isEqualTo(request.getPrice());
        assertThat(response.getCategory()).isEqualTo(request.getCategory());
        assertThat(response.getBrand()).isEqualTo(request.getBrand());
    }


    @Test
    @Description("Cenário 7: O ID retornado deve ser sempre um número inteiro")
    void deveRetornarIdComoInteiro() {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        assertThat(response.getId())
                .as("ID deve ser inteiro positivo")
                .isNotNull()
                .isPositive();
        assertThat(response.getId()).isInstanceOf(Integer.class);
    }

    @Test
    @Description("Cenário 8: O título retornado deve ser sempre string")
    void deveRetornarTituloComoString() {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        assertThat(response.getTitle())
                .as("Título deve ser string não vazio")
                .isNotBlank()
                .isInstanceOf(String.class);
    }

    @Test
    @Description("Cenário 9: O rating retornado deve estar dentro do intervalo válido")
    void deveRetornarRatingValido() {
        ProductRequest request = TestDataBuilder.produtoValido();

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        if (response.getRating() != null) {
            AssertionsHelper.assertRatingEstaDentroDoIntervaloValido(response.getRating());
        }
    }


    /**
     * Os cenários abaixo validam o contrato documentado: payload inválido deve retornar 400.
     * Se a API retornar 201, o teste falha e evidencia o bug.
     */

    @Test
    @BugFound("BUG #2a: API aceita título vazio, mas o contrato exige 400")
    @Description("Cenário 10: Requisição com título vazio deve retornar 400")
    void deveRetornar400AoCriarProdutoComTituloVazio() {
        ProductRequest request = TestDataBuilder.produtoValido();
        request.setTitle("");

        ApiHelper.enviarRaw(
                requestSpec,
                "/products/add",
                request,
                400
        );
    }

    @Test
    @BugFound("BUG #2b: API aceita preço nulo, mas o contrato exige 400")
    @Description("Cenário 11: Requisição sem preço deve retornar 400")
    void deveRetornar400AoCriarProdutoSemPreco() {
        ProductRequest request = TestDataBuilder.produtoValido();
        request.setPrice(null);

        ApiHelper.enviarRaw(
                requestSpec,
                "/products/add",
                request,
                400
        );
    }

    @Test
    @BugFound("BUG #2c: API aceita categoria nula, mas o contrato exige 400")
    @Description("Cenário 12: Requisição sem categoria deve retornar 400")
    void deveRetornar400AoCriarProdutoSemCategoria() {
        ProductRequest request = TestDataBuilder.produtoValido();
        request.setCategory(null);

        ApiHelper.enviarRaw(
                requestSpec,
                "/products/add",
                request,
                400
        );
    }

    @Test
    @Description("Cenário 13: [Comportamento atual] API aceita título vazio e retorna 201")
    void deveDocumentarComportamentoAtualTituloVazio() {
        ProductRequest request = TestDataBuilder.produtoValido();
        request.setTitle("");

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        assertThat(response).isNotNull();
        assertThat(response.getId()).isPositive();
    }

    @Test
    @Description("Cenário 14: [Comportamento atual] API aceita preço nulo e retorna 201")
    void deveDocumentarComportamentoAtualSemPreco() {
        ProductRequest request = TestDataBuilder.produtoValido();
        request.setPrice(null);

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        assertThat(response).isNotNull();
        assertThat(response.getId()).isPositive();
    }

    @Test
    @Description("Cenário 15: [Comportamento atual] API aceita categoria nula e retorna 201")
    void deveDocumentarComportamentoAtualSemCategoria() {
        ProductRequest request = TestDataBuilder.produtoValido();
        request.setCategory(null);

        Product response = ApiHelper.enviar(
                requestSpec,
                "/products/add",
                request,
                201,
                Product.class
        );

        assertThat(response).isNotNull();
        assertThat(response.getId()).isPositive();
    }
}


