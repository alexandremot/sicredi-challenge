#!/bin/bash

# Script para executar testes via Docker Compose
# Uso: ./run-tests-docker.sh

set -e

echo "🐳 SICREDI CHALLENGE - Execução via Docker"
echo "=========================================="
echo ""

# Verificar se Docker está instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker não está instalado. Instale antes de continuar."
    exit 1
fi

# Verificar se Docker Compose está instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não está instalado. Instale antes de continuar."
    exit 1
fi

echo "✅ Docker encontrado: $(docker --version)"
echo "✅ Docker Compose encontrado: $(docker-compose --version)"
echo ""

# Build e execução
echo "🚀 Fazendo build e executando testes via Docker..."
echo ""

docker-compose up -d

echo ""
echo "⏳ Aguardando testes completarem..."
sleep 10

# Verificar status do container
echo ""
echo "📊 Status do container:"
docker-compose ps

echo ""
echo "✅ Testes concluídos via Docker!"
echo ""
echo "📊 Relatório Allure disponível em:"
echo "   ./target/site/allure-maven-plugin/index.html"
echo ""
echo "Para abrir no navegador:"
echo "   open ./target/site/allure-maven-plugin/index.html"
echo ""
echo "Para parar o container:"
echo "   docker-compose down"
echo ""

