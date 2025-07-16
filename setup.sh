#!/bin/sh

echo "Setup - Mão Amiga"

# Verificar Java
if ! command -v java >/dev/null 2>&1; then
    echo ""
    echo "ERRO: Java não encontrado"
    echo ""
    echo "Instale o Java 17:"
    echo "  Ubuntu/Debian: sudo apt install openjdk-17-jdk"
    echo "  Fedora/RHEL:   sudo dnf install java-17-openjdk-devel"
    echo "  Arch:          sudo pacman -S jdk17-openjdk"
    echo ""
    echo "Depois execute o setup novamente."
    exit 1
fi

# Verificar PostgreSQL
if ! command -v psql >/dev/null 2>&1; then
    echo ""
    echo "ERRO: PostgreSQL não encontrado"
    echo ""
    echo "Instale o PostgreSQL:"
    echo "  Ubuntu/Debian: sudo apt install postgresql postgresql-contrib"
    echo "  Fedora/RHEL:   sudo dnf install postgresql postgresql-server"
    echo "  Arch:          sudo pacman -S postgresql"
    echo ""
    echo "Inicie o serviço: sudo systemctl start postgresql"
    echo "Depois execute o setup novamente."
    exit 1
fi

# Verificar se PostgreSQL está rodando
if ! pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
    echo ""
    echo "ERRO: PostgreSQL não está rodando"
    echo "Inicie: sudo systemctl start postgresql"
    exit 1
fi

# Configurar banco
echo "Configurando banco de dados..."
sudo -u postgres psql << EOF
DROP DATABASE IF EXISTS mao_amiga_db;
DROP DATABASE IF EXISTS mao_amiga_test_db;
DROP USER IF EXISTS mao_amiga_user;
CREATE DATABASE mao_amiga_db;
CREATE DATABASE mao_amiga_test_db;
CREATE USER mao_amiga_user WITH PASSWORD 'mao_amiga_password';
GRANT ALL PRIVILEGES ON DATABASE mao_amiga_db TO mao_amiga_user;
GRANT ALL PRIVILEGES ON DATABASE mao_amiga_test_db TO mao_amiga_user;
ALTER USER mao_amiga_user CREATEDB;

-- Conceder permissões no schema public para evitar "permission denied"
GRANT ALL PRIVILEGES ON SCHEMA public TO mao_amiga_user;
GRANT CREATE ON SCHEMA public TO mao_amiga_user;

-- Definir privilégios padrão para objetos futuros
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO mao_amiga_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO mao_amiga_user;
\q
EOF

# Dar permissões no banco
echo "Aplicando permissões específicas..."
sudo -u postgres psql -d mao_amiga_db << EOF
GRANT ALL PRIVILEGES ON SCHEMA public TO mao_amiga_user;
GRANT CREATE ON SCHEMA public TO mao_amiga_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO mao_amiga_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO mao_amiga_user;
\q
EOF

sudo -u postgres psql -d mao_amiga_test_db << EOF
GRANT ALL PRIVILEGES ON SCHEMA public TO mao_amiga_user;
GRANT CREATE ON SCHEMA public TO mao_amiga_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO mao_amiga_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO mao_amiga_user;
\q
EOF

# Limpar build anterior se existir
if [ -d "src" ]; then
    echo "Limpando build anterior..."
    cd src
    ./mvnw clean -q
    cd ..
fi

echo ""
echo "Setup concluído com sucesso!"
echo "Execute: ./run.sh"
