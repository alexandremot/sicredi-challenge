#!/bin/bash

# Script para executar testes localmente com Maven e servir relatório
# Uso: ./run-tests-local.sh

set -e

echo "🧪 SICREDI CHALLENGE - Execução Local (Maven)"
echo "=============================================="
echo ""

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não está instalado. Instale antes de continuar."
    exit 1
fi

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não está instalado. Instale antes de continuar."
    exit 1
fi

echo "✅ Maven encontrado: $(mvn --version | head -1)"
echo "✅ Java encontrado: $(java -version 2>&1 | head -1)"
echo ""

# Executar testes
echo "🚀 Executando testes..."
echo ""

mvn clean test allure:report

echo ""
echo "✅ Testes concluídos com sucesso!"
echo ""
echo "📊 Iniciando servidor Allure na porta 4040..."
echo ""

# Iniciar servidor Allure automaticamente
mvn allure:serve

