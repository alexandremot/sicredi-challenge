package org.sicredi.challenge.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.sicredi.challenge.model.UserResponse;
import org.sicredi.challenge.utils.ApiHelper;
import org.sicredi.challenge.utils.AssertionsHelper;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Usuários")
public class UsersTest extends BaseTest {

    @Test
    @Description("Deve retornar lista de usuários com status 200")
    void deveRetornarListaDeUsuariosComSucesso() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertListaUsuariosNaoEstaVazia(response);
    }

    @Test
    @Description("Todos os usuários devem conter username e password preenchidos")
    void todosUsuariosDevemTerUsernameEPassword() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertTodosOsUsuariosTemUsernameEPasswordPreenchidos(response);
    }

    @Test
    @Description("Deve retornar metadados de paginação na resposta")
    void deveRetornarMetadadosDePaginacao() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertMetadadosPaginacaoDeUsuariosValidos(response);
    }

    @Test
    @Description("Deve retornar Content-Type application/json no header da resposta")
    void deveRetornarContentTypeJson() {
        ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
    }

    @Test
    @Description("Quantidade de usuários retornados deve ser igual ao valor do campo limit")
    void deveRetornarQuantidadeUsuariosIgualLimit() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertQuantidadeDeUsuariosRetornadosEhIgualAoLimit(response);
    }

    @Test
    @Description("Total de usuários deve ser maior que o limit")
    void totalDeveSermMaiorQueLimite() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertTotalDeUsuariosMaiorQueLimitRetornado(response);
    }

    @Test
    @Description("Campos de paginação devem ser do tipo inteiro")
    void camposPaginacaoDevemSerInteiros() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertCamposDePaginacaoSaoInteiros(response);
    }

    @Test
    @Description("Todos os usernames devem ser únicos na lista")
    void usernamesDevemSerUnicos() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertTodosOsUsernamesSaoUnicos(response);
    }

    @Test
    @Description("Todas as passwords devem ser únicas na lista")
    void senhasDevemSerUnicas() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertTodosAsPasswordsSaoUnicas(response);
    }

    @Test
    @Description("Username e password devem ser do tipo string")
    void camposAutenticacaoDevemSerStrings() {
        UserResponse response = ApiHelper.obter(requestSpec, "/users", 200, UserResponse.class);
        AssertionsHelper.assertTodosOsUsernamesSaoDoTipoString(response);
    }

    @Test
    @Description("Requisição para recurso inexistente deve retornar 404")
    void recursoInexistenteDeveretornar404() {
        var statusCode = RestAssured
                .given()
                    .spec(requestSpec)
                .when()
                    .get("/usuariosInexistente")
                .then()
                    .extract()
                    .statusCode();
        
        assertThat(statusCode).isEqualTo(404);
    }
}

