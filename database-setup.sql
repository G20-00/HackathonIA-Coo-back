-- Script SQL para crear la base de datos manualmente (OPCIONAL)
-- La aplicación puede crear la base de datos automáticamente con createDatabaseIfNotExist=true
-- Este script es solo por si quieres configurarla manualmente

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS coomeva_hackathon 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE coomeva_hackathon;

-- Verificar la creación
SHOW DATABASES LIKE 'coomeva_hackathon';

-- Nota: Las tablas se crean automáticamente por JPA/Hibernate al iniciar la aplicación

-- Para borrar la base de datos y empezar de nuevo (CUIDADO: Borra todos los datos)
-- DROP DATABASE IF EXISTS coomeva_hackathon;
