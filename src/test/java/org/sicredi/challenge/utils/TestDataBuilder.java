package org.sicredi.challenge.utils;

import org.sicredi.challenge.model.LoginRequest;
import org.sicredi.challenge.model.ProductRequest;

/**
 * Factory para construção de objetos de teste com valores padrão.
 */
public class TestDataBuilder {

    public static LoginRequest loginValido() {
        return LoginRequest.builder()
                .username(TestConstants.USERNAME)
                .password(TestConstants.PASSWORD)
                .build();
    }

    public static LoginRequest loginInvalido() {
        return LoginRequest.builder()
                .username("usuario_invalido")
                .password("senha_invalida")
                .build();
    }

    public static LoginRequest loginCustomizado(String username, String password) {
        return LoginRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    public static LoginRequest loginComPasswordInvalido() {
        return LoginRequest.builder()
                .username(TestConstants.USERNAME)
                .password("senha_incorreta_" + System.currentTimeMillis())
                .build();
    }

    public static LoginRequest loginComUsernameVazio() {
        return LoginRequest.builder()
                .username("")
                .password(TestConstants.PASSWORD)
                .build();
    }

    public static LoginRequest loginSemUsername() {
        return LoginRequest.builder()
                .password(TestConstants.PASSWORD)
                .build();
    }

    public static LoginRequest loginComPasswordVazio() {
        return LoginRequest.builder()
                .username(TestConstants.USERNAME)
                .password("")
                .build();
    }

    public static LoginRequest loginSemPassword() {
        return LoginRequest.builder()
                .username(TestConstants.USERNAME)
                .build();
    }

    public static ProductRequest produtoValido() {
        return ProductRequest.builder()
                .title(TestConstants.randomProductTitle())
                .description(TestConstants.PRODUCT_DESCRIPTION)
                .price(TestConstants.PRODUCT_PRICE)
                .discountPercentage(TestConstants.PRODUCT_DISCOUNT)
                .rating(TestConstants.PRODUCT_RATING)
                .stock(TestConstants.PRODUCT_STOCK)
                .brand(TestConstants.randomProductBrand())
                .category(TestConstants.randomProductCategory())
                .thumbnail(TestConstants.PRODUCT_THUMBNAIL)
                .build();
    }
}

