-- Apoio operacional (NÃO executado pelo Flyway): cria schema e usuário da aplicação em um MySQL 8
-- já existente. Execute como administrador do banco, trocando a senha antes:
--   mysql -u root -p < database/scripts/criar-banco-e-usuario.sql
-- As tabelas são criadas pelo Flyway ao iniciar o backend.
CREATE DATABASE IF NOT EXISTS lavicameta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'lavicameta'@'%' IDENTIFIED BY 'TROQUE_ESTA_SENHA';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES, DROP ON lavicameta.* TO 'lavicameta'@'%';
FLUSH PRIVILEGES;
