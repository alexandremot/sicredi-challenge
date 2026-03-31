package org.sicredi.challenge.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar testes que validam contrato onde já existe bug conhecido.
 * Diferente de @Disabled, estes testes DEVEM RODAR e podem falhar enquanto
 * a API estiver divergente da documentação.
 * Uso:
 * <pre>
 * {@code
 * @BugFound("BUG #1: API retorna 200 em vez de 401 sem autenticacao")
 * void deveRetornar401SemToken() { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BugFound {
    /**
     * Descrição clara do bug documentado
     */
    String value();
}


