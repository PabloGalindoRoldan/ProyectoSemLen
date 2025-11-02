-- Schema for seminario_2024_1
-- Run in MySQL to create DB and tables

CREATE DATABASE IF NOT EXISTS seminario_2024_1 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE seminario_2024_1;

-- roles table (simple)
CREATE TABLE IF NOT EXISTS roles (
  codigo INT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  activo BOOLEAN DEFAULT TRUE
);

-- usuarios table
CREATE TABLE IF NOT EXISTS usuarios (
  usuario VARCHAR(100) PRIMARY KEY,
  contrasena VARCHAR(255) NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  rol_codigo INT,
  activo BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (rol_codigo) REFERENCES roles(codigo)
);

-- sample data
INSERT IGNORE INTO roles (codigo, nombre, activo) VALUES
(1, 'ADMIN', 1),
(2, 'ESTUDIANTE', 1),
(3, 'INVITADO', 1),
(4, 'DONANTE', 1),
(5, 'VOLUNTARIO', 1);

INSERT IGNORE INTO usuarios (usuario, contrasena, nombre, email, rol_codigo, activo) VALUES
('admin', '1234', 'Admin', 'admin@unrn.edu.ar', 1, 1);
