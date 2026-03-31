package org.sicredi.challenge.utils;

import org.sicredi.challenge.model.LoginResponse;
import org.sicredi.challenge.model.Product;
import org.sicredi.challenge.model.ProductResponse;
import org.sicredi.challenge.model.UserResponse;

import static org.assertj.core.api.Assertions.*;

public class AssertionsHelper {

    public static void assertLoginResponseComAccessTokenERefreshTokenValidos(LoginResponse response) {
        assertThat(response)
                .as("Resposta de login não deve ser nula")
                .isNotNull();
        assertThat(response.getAccessToken())
                .as("Access token não deve estar em branco")
                .isNotBlank();
        assertThat(response.getRefreshToken())
                .as("Refresh token não deve estar em branco")
                .isNotBlank();
    }

    public static void assertLoginResponseComIdEmailNomeCompleto(LoginResponse response) {
        assertThat(response.getId())
                .as("ID do usuário não deve ser nulo")
                .isNotNull();
        assertThat(response.getEmail())
                .as("Email do usuário não deve estar em branco")
                .isNotBlank();
        assertThat(response.getFirstName())
                .as("Primeiro nome não deve estar em branco")
                .isNotBlank();
        assertThat(response.getLastName())
                .as("Sobrenome não deve estar em branco")
                .isNotBlank();
    }

    public static void assertLoginUsernameEhIgualAoEsperado(LoginResponse response, String expectedUsername) {
        assertThat(response.getUsername())
                .as("Username deve ser igual ao esperado")
                .isEqualTo(expectedUsername);
    }

    public static void assertProdutoComIdTitlePriceECategoryValidos(Product product) {
        assertThat(product)
                .as("Produto não deve ser nulo")
                .isNotNull();
        assertThat(product.getId())
                .as("ID do produto não deve ser nulo")
                .isNotNull();
        assertThat(product.getTitle())
                .as("Título do produto não deve estar em branco")
                .isNotBlank();
        assertThat(product.getPrice())
                .as("Preço deve ser positivo")
                .isPositive();
        assertThat(product.getCategory())
                .as("Categoria não deve estar em branco")
                .isNotBlank();
    }

    public static void assertProdutoCriadoComDadosInformados(Product response, Object request) {
        assertThat(response)
                .as("Resposta do produto criado não deve ser nula")
                .isNotNull();
        assertThat(response.getId())
                .as("ID do produto criado não deve ser nulo")
                .isNotNull();
        
        // Se request é um ProductRequest, validar que os dados foram preservados
        if (request instanceof org.sicredi.challenge.model.ProductRequest) {
            org.sicredi.challenge.model.ProductRequest productRequest = (org.sicredi.challenge.model.ProductRequest) request;
            
            assertThat(response.getTitle())
                    .as("Título da resposta deve corresponder ao título enviado")
                    .isEqualTo(productRequest.getTitle());
            assertThat(response.getPrice())
                    .as("Preço da resposta deve corresponder ao preço enviado")
                    .isEqualTo(productRequest.getPrice());
            assertThat(response.getCategory())
                    .as("Categoria da resposta deve corresponder à categoria enviada")
                    .isEqualTo(productRequest.getCategory());
        }
    }

    public static void assertProdutoComIdEsperado(Product product, int expectedId) {
        assertThat(product.getId())
                .as("ID do produto deve ser igual ao esperado")
                .isEqualTo(expectedId);
    }

    public static void assertListaProdutosNaoEstaVazia(ProductResponse response) {
        assertThat(response)
                .as("Resposta de produtos não deve ser nula")
                .isNotNull();
        assertThat(response.getProducts())
                .as("Lista de produtos não deve estar vazia")
                .isNotEmpty();
        assertThat(response.getTotal())
                .as("Total de produtos deve ser positivo")
                .isPositive();
    }

    public static void assertMetadadosPaginacaoDeProdutos(ProductResponse response, int expectedLimit) {
        assertThat(response.getLimit())
                .as("Limit deve corresponder ao esperado")
                .isEqualTo(expectedLimit);
        assertThat(response.getSkip())
                .as("Skip deve ser >= 0")
                .isGreaterThanOrEqualTo(0);
        assertThat(response.getTotal())
                .as("Total deve ser positivo")
                .isPositive();
    }

    public static void assertListaUsuariosNaoEstaVazia(UserResponse response) {
        assertThat(response)
                .as("Resposta de usuários não deve ser nula")
                .isNotNull();
        assertThat(response.getUsers())
                .as("Lista de usuários não deve estar vazia")
                .isNotEmpty();
    }


    public static void assertTodosOsUsuariosTemUsernameEPasswordPreenchidos(UserResponse response) {
        response.getUsers().forEach(user -> {
            assertThat(user.getUsername())
                    .as("Username não deve estar em branco")
                    .isNotBlank();
            assertThat(user.getPassword())
                    .as("Password não deve estar em branco")
                    .isNotBlank();
        });
    }

    public static void assertMetadadosPaginacaoDeUsuariosValidos(UserResponse response) {
        assertThat(response.getTotal())
                .as("Total de usuários deve ser positivo")
                .isPositive();
        assertThat(response.getSkip())
                .as("Skip deve ser >= 0")
                .isGreaterThanOrEqualTo(0);
        assertThat(response.getLimit())
                .as("Limit deve ser positivo")
                .isPositive();
    }

    public static void assertTotalDeUsuariosMaiorQueLimitRetornado(UserResponse response) {
        assertThat(response.getTotal())
                .as("Total de usuários deve ser maior que limit")
                .isGreaterThan(response.getLimit());
    }

    public static void assertQuantidadeDeUsuariosRetornadosEhIgualAoLimit(UserResponse response) {
        int quantidade = response.getUsers().size();
        int limit = response.getLimit();
        assertThat(quantidade)
                .as("Quantidade de usuários retornados deve ser igual a limit")
                .isEqualTo(limit);
    }

    public static void assertTodosOsUsernamesSaoUnicos(UserResponse response) {
        var usernames = response.getUsers().stream()
                .map(user -> user.getUsername())
                .toList();
        
        assertThat(usernames)
                .as("Todos os usernames devem ser únicos")
                .doesNotHaveDuplicates();
    }

    public static void assertTodosAsPasswordsSaoUnicas(UserResponse response) {
        var passwords = response.getUsers().stream()
                .map(user -> user.getPassword())
                .toList();
        
        assertThat(passwords)
                .as("Todas as passwords devem ser únicas")
                .doesNotHaveDuplicates();
    }

    public static void assertTodosOsUsernamesSaoDoTipoString(UserResponse response) {
        response.getUsers().forEach(user -> {
            assertThat(user.getUsername())
                    .as("Username deve ser string")
                    .isInstanceOf(String.class);
            assertThat(user.getPassword())
                    .as("Password deve ser string")
                    .isInstanceOf(String.class);
        });
    }

    public static void assertCamposDePaginacaoSaoInteiros(UserResponse response) {
        assertThat(response.getTotal())
                .as("Total deve ser inteiro")
                .isInstanceOf(Integer.class);
        assertThat(response.getSkip())
                .as("Skip deve ser inteiro")
                .isInstanceOf(Integer.class);
        assertThat(response.getLimit())
                .as("Limit deve ser inteiro")
                .isInstanceOf(Integer.class);
    }

    public static void assertProdutoComTitleCategoryEDescricaoNaoVazios(Product product) {
        assertThat(product.getTitle())
                .as("Título não deve estar vazio")
                .isNotBlank();
        assertThat(product.getCategory())
                .as("Categoria não deve estar vazia")
                .isNotBlank();
        assertThat(product.getDescription())
                .as("Descrição não deve estar vazia")
                .isNotBlank();
    }

    public static void assertCampoNumericoEhMaiorOuIgualAZero(Double valor, String nomeCampo) {
        assertThat(valor)
                .as(nomeCampo + " deve ser >= 0")
                .isGreaterThanOrEqualTo(0.0);
    }

    public static void assertRatingEstaDentroDoIntervaloValido(Double rating) {
        assertThat(rating)
                .as("Rating deve estar entre 0 e 5")
                .isGreaterThanOrEqualTo(0.0)
                .isLessThanOrEqualTo(5.0);
    }

    public static void assertProdutoTemInformacoesDeEnvioGarantiaEDisponibilidade(Product product) {
        assertThat(product.getShippingInformation())
                .as("Informações de envio não devem estar vazias")
                .isNotBlank();
        assertThat(product.getWarrantyInformation())
                .as("Informações de garantia não devem estar vazias")
                .isNotBlank();
        assertThat(product.getAvailabilityStatus())
                .as("Status de disponibilidade não deve estar vazio")
                .isNotBlank();
    }

    public static void assertDimensoesDoProdutoSaoValidas(Product product) {
        assertThat(product.getDimensions())
                .as("Dimensões não devem ser nulas")
                .isNotNull();
        
        var dims = product.getDimensions();
        assertThat(dims.getWidth())
                .as("Width deve ser > 0")
                .isGreaterThan(0.0);
        assertThat(dims.getHeight())
                .as("Height deve ser > 0")
                .isGreaterThan(0.0);
        assertThat(dims.getDepth())
                .as("Depth deve ser > 0")
                .isGreaterThan(0.0);
    }

    public static void assertProdutoTemReviewsComEstruturasValidas(Product product) {
        assertThat(product.getReviews())
                .as("Reviews não devem ser nulos")
                .isNotNull()
                .isNotEmpty();
        
        product.getReviews().forEach(review -> {
            assertThat(review.getRating())
                    .as("Rating do review deve estar entre 1 e 5")
                    .isGreaterThanOrEqualTo(1)
                    .isLessThanOrEqualTo(5);
            assertThat(review.getComment())
                    .as("Comment do review não deve estar vazio")
                    .isNotBlank();
            assertThat(review.getReviewerName())
                    .as("Reviewer name não deve estar vazio")
                    .isNotBlank();
            assertThat(review.getReviewerEmail())
                    .as("Reviewer email não deve estar vazio")
                    .isNotBlank();
        });
    }

    public static void assertCampoNumericoEhDoTipoNumber(Double valor, String nomeCampo) {
        assertThat(valor)
                .as(nomeCampo + " deve ser do tipo número")
                .isInstanceOf(Number.class);
    }

    public static void assertStockEhValidoEMaiorOuIgualAZero(Integer stock) {
        assertThat(stock)
                .as("Stock deve ser >= 0")
                .isGreaterThanOrEqualTo(0);
    }
}

