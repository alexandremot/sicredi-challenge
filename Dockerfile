# Dockerfile Simples - Executar Testes
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar código fonte e configurações
COPY src ./src
COPY src/test/resources ./src/test/resources

# Executar testes e gerar relatório Allure
RUN mvn clean test allure:report -q

# Expor diretório de testes e relatório
VOLUME ["/app/target/allure-results", "/app/target/site"]

# Comando padrão
CMD ["echo", "Testes concluídos com sucesso!"]
