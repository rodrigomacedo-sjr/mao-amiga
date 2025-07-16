-- Script para configurar o banco PostgreSQL para o projeto Mão Amiga

-- 1. Conecte-se ao PostgreSQL como superusuário (postgres)
-- psql -U postgres

-- 2. Criar o banco de dados
CREATE DATABASE mao_amiga_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'pt_BR.UTF-8'
    LC_CTYPE = 'pt_BR.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- 3. Criar usuário específico para a aplicação
CREATE USER mao_amiga_user WITH PASSWORD 'mao_amiga_password';

-- 4. Conceder privilégios ao usuário
GRANT ALL PRIVILEGES ON DATABASE mao_amiga_db TO mao_amiga_user;

-- 5. Conectar ao banco criado
\c mao_amiga_db;

-- 6. Conceder privilégios no schema public
GRANT ALL ON SCHEMA public TO mao_amiga_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO mao_amiga_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO mao_amiga_user;

-- 7. Definir privilégios padrão para futuras tabelas
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO mao_amiga_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO mao_amiga_user;

-- Verificar se tudo foi criado corretamente
SELECT datname FROM pg_database WHERE datname = 'mao_amiga_db';
SELECT usename FROM pg_user WHERE usename = 'mao_amiga_user';
