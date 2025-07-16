#!/bin/bash

echo "🐘 Setup PostgreSQL - Mão Amiga"
echo ""

# Verificar se PostgreSQL está instalado
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL não está instalado!"
    echo "💡 Instale com: sudo apt install postgresql postgresql-contrib"
    exit 1
fi

echo "📋 Configurando banco..."

# Executar comandos SQL
sudo -u postgres psql << EOF
CREATE DATABASE mao_amiga_db ENCODING 'UTF8';
CREATE DATABASE mao_amiga_test_db ENCODING 'UTF8';
CREATE USER mao_amiga_user WITH PASSWORD 'mao_amiga_password';
GRANT ALL PRIVILEGES ON DATABASE mao_amiga_db TO mao_amiga_user;
GRANT ALL PRIVILEGES ON DATABASE mao_amiga_test_db TO mao_amiga_user;
ALTER USER mao_amiga_user CREATEDB;
\q
EOF

if [ $? -eq 0 ]; then
    echo "✅ PostgreSQL configurado!"
    echo ""
    echo "📊 Configuração:"
    echo "   Database: mao_amiga_db (produção)"
    echo "   Database: mao_amiga_test_db (testes)"
    echo "   User: mao_amiga_user"
    echo "   Password: mao_amiga_password"
    echo ""
    echo "🚀 Execute agora: ./run-simple.sh"
else
    echo "❌ Erro na configuração!"
    echo "💡 Verifique se PostgreSQL está rodando: sudo systemctl start postgresql"
fi
