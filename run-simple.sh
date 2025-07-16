#!/bin/bash

echo "🚀 Mão Amiga - PostgreSQL"
echo ""

cd /home/roger/uel/engsoft2/mao-beck/src

# Verificar se PostgreSQL está rodando
if ! pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
    echo "⚠️  PostgreSQL não está rodando!"
    echo "💡 Execute: sudo systemctl start postgresql"
    echo ""
fi

echo "📋 Compilando..."
./mvnw clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Compilado!"
    echo ""
    echo "🏃 Executando..."
    echo "📍 API: http://localhost:8080"
    echo "📍 Teste: http://localhost:8080/api/test/hello"
    echo ""
    echo "⚠️  Ctrl+C para parar"
    echo ""
    
    ./mvnw spring-boot:run -q
else
    echo "❌ Erro na compilação!"
    exit 1
fi
