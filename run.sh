#!/bin/sh

cd src

# Verificar PostgreSQL
if ! pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
    echo "Erro: PostgreSQL não está rodando"
    echo "Inicie: sudo systemctl start postgresql"
    exit 1
fi

echo "Compilando..."
./mvnw clean compile -q || exit 1

echo "Executando em http://localhost:8080"
./mvnw spring-boot:run -q
