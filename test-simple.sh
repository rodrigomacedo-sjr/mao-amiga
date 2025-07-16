#!/bin/bash

echo "🧪 Executando testes - Mão Amiga"
echo ""

cd src

# Verificar se banco de testes existe
echo "📋 Verificando banco de testes..."
if ! sudo -u postgres psql -d mao_amiga_test_db -c '\q' 2>/dev/null; then
    echo "🔧 Criando banco de testes..."
    sudo -u postgres createdb mao_amiga_test_db
    sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE mao_amiga_test_db TO mao_amiga_user;"
fi

# Executar testes
echo "🚀 Executando testes..."
./mvnw test -Dspring.profiles.active=test

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Testes executados com sucesso!"
else
    echo ""
    echo "❌ Alguns testes falharam!"
    echo "💡 Verifique se PostgreSQL está rodando e o banco de testes existe"
fi
