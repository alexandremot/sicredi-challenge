package org.sicredi.challenge.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.sicredi.challenge.model.LoginRequest;

import java.util.Date;

public class TokenManager {

    private static String token;

    private TokenManager() {}

    public static String getToken() {
        if (token == null || token.isBlank() || isTokenExpired(token)) {
            token = fetchToken();
        }
        return token;
    }

    /**
     * Verifica se o token JWT está expirado decodificando o claim 'exp'.
     * Se o token não puder ser decodificado (formato inválido), retorna true
     * para forçar uma nova requisição.
     */
    private static boolean isTokenExpired(String jwtToken) {
        try {
            // Em jjwt 0.12.3, usamos parseUnsecuredClaims para tokens sem assinatura
            Claims claims = Jwts.parser()
                    .build()
                    .parseUnsecuredClaims(jwtToken)
                    .getPayload();

            Date expirationDate = claims.getExpiration();
            if (expirationDate == null) {
                return false; // Token sem expiração
            }

            // Verifica se a data de expiração é anterior ao horário atual
            return expirationDate.before(new Date());
        } catch (Exception e) {
            // Se não conseguir decodificar, considera expirado para segurança
            return true;
        }
    }

    private static String fetchToken() {
        LoginRequest login = LoginRequest.builder()
                .username(TestConstants.USERNAME)
                .password(TestConstants.PASSWORD)
                .build();

        try {
            return RestAssured
                    .given()
                    .baseUri(TestConstants.BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(login)
                    .when()
                    .post("/auth/login")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("accessToken");
        } catch (Exception e) {
            throw new RuntimeException(
                    "Falha ao obter token de autenticação. " +
                    "Verifique as credenciais e se o servidor está acessível.",
                    e
            );
        }
    }
}