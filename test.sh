#!/bin/sh

cd src

# Verificar PostgreSQL
if ! pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
    echo "Erro: PostgreSQL não está rodando"
    echo "Inicie: sudo systemctl start postgresql"
    exit 1
fi

# Compilar antes de testar
echo "Compilando..."
./mvnw clean compile -q || exit 1

echo "Executando testes..."
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED"
./mvnw test -Dspring.profiles.active=test -Dlogging.level.sun.misc.Unsafe=OFF
