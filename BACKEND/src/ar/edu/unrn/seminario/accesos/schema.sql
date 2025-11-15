-- Recreate seminario database
DROP DATABASE IF EXISTS seminario;
CREATE DATABASE seminario DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE seminario;

-- Drop tables in reverse dependency order (children first)
DROP TABLE IF EXISTS articulos;
DROP TABLE IF EXISTS visitas;
DROP TABLE IF EXISTS ordenes_retiro;
DROP TABLE IF EXISTS donaciones;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;

-- Create tables
CREATE TABLE roles (
  codigo INT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE usuarios (
  usuario VARCHAR(100) PRIMARY KEY,
  contrasena VARCHAR(255) NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  rol_codigo INT,
  activo BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (rol_codigo) REFERENCES roles(codigo)
);

CREATE TABLE pedidos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  descripcion TEXT,
  observaciones TEXT,
  necesitaVehiculo BOOLEAN DEFAULT FALSE,
  donante_username VARCHAR(100),
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  activo BOOLEAN DEFAULT TRUE,
  puntaje_total INT DEFAULT 0,
  FOREIGN KEY (donante_username) REFERENCES usuarios(usuario)
);

-- TipoDonacion ajustado al enum de Java
CREATE TABLE donaciones (
  id INT PRIMARY KEY AUTO_INCREMENT,
  pedido_id INT,
  tipoDonacion ENUM(
    'ROPA', 'CALZADO', 'ALIMENTOS', 'JUGUETES', 'MUEBLES',
    'ELECTRONICA', 'HIGIENE', 'MEDICAMENTOS', 'OTRO'
  ),
  categoria VARCHAR(100),
  puntaje INT,
  FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

CREATE TABLE ordenes_retiro (
  id INT PRIMARY KEY AUTO_INCREMENT,
  pedido_id INT,
  voluntario_username VARCHAR(100),
  fecha_generacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(50),
  FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
  FOREIGN KEY (voluntario_username) REFERENCES usuarios(usuario)
);

CREATE TABLE visitas (
  id INT PRIMARY KEY AUTO_INCREMENT,
  visitante VARCHAR(100),
  fechaHora DATETIME,
  motivo TEXT,
  confirmada BOOLEAN DEFAULT FALSE,
  cantidadBienesRecogidos INT DEFAULT 0,
  observaciones TEXT,
  orden_retiro_id INT,
  visitaFinal BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (orden_retiro_id) REFERENCES ordenes_retiro(id) ON DELETE CASCADE
);

CREATE TABLE articulos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  visita_id INT,
  nombre VARCHAR(255),
  cantidad INT,
  tipoDonacion ENUM(
    'ROPA', 'CALZADO', 'ALIMENTOS', 'JUGUETES', 'MUEBLES',
    'ELECTRONICA', 'HIGIENE', 'MEDICAMENTOS', 'OTRO'
  ),
  FOREIGN KEY (visita_id) REFERENCES visitas(id) ON DELETE CASCADE
);

-- Insert initial data
INSERT INTO roles (codigo, nombre, activo) VALUES
(1, 'ADMIN', 1),
(4, 'DONANTE', 1),
(5, 'VOLUNTARIO', 1);

INSERT INTO usuarios (usuario, contrasena, nombre, email, rol_codigo, activo) VALUES
('admin', '1234', 'Admin', 'admin@unrn.edu.ar', 1, 1),
('ldifabio', '4', 'Lucas', 'ldifabio@unrn.edu.ar', 4, 1),
('bjgorosito', '1234', 'Bruno', 'bjgorosito@unrn.edu.ar', 5, 1),
('pgalindo', '5678', 'Pablo', 'pablogalindo90@gmail.com', 4, 1),
('mvoluntario', 'v123', 'Voluntario Uno', 'vol1@example.com', 5, 1),
('donante1', 'd123', 'Donante Uno', 'don1@example.com', 4, 1),
('vol2', 'v456', 'Voluntario Dos', 'vol2@example.com', 5, 1);

-- Pedido 1
INSERT INTO pedidos (descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total)
VALUES ('Pedido de ropa y alimentos', 'Ropa usada en buen estado, alimentos no perecederos', 1, 'pgalindo', NOW(), 1, 30);
SET @pedido1_id = LAST_INSERT_ID();

INSERT INTO donaciones (pedido_id, tipoDonacion, categoria, puntaje) VALUES
(@pedido1_id, 'ROPA', 'INDUMENTARIA', 10),
(@pedido1_id, 'ALIMENTOS', 'NO_PERECEDERO', 20);

-- Pedido 2
INSERT INTO pedidos (descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total)
VALUES ('Pedido de muebles', 'Camas, mesas y sillas en buen estado', 1, 'donante1', NOW(), 1, 50);
SET @pedido2_id = LAST_INSERT_ID();

INSERT INTO donaciones (pedido_id, tipoDonacion, categoria, puntaje) VALUES
(@pedido2_id, 'MUEBLES', 'HOGAR', 30),
(@pedido2_id, 'MUEBLES', 'HOGAR', 20);

INSERT INTO ordenes_retiro (pedido_id, voluntario_username, fecha_generacion, estado) VALUES
(@pedido1_id, 'mvoluntario', NOW(), 'COMPLETADO'),
(@pedido2_id, 'vol2', NOW(), 'EN_EJECUCION');

-- Visits and items
INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES
('Familia Perez', NOW(), 'Entrega de ropa', 1, 10, 'Todo embalado', 1, 0);
SET @visita1_id = LAST_INSERT_ID();
INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES
(@visita1_id, 'Camisa', 5, 'ROPA'),
(@visita1_id, 'Pantalon', 3, 'ROPA');

INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES
('Casa Lopez', NOW(), 'Retiro de muebles', 0, 0, 'Programar visita', 2, 0);
SET @visita2_id = LAST_INSERT_ID();
INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES
(@visita2_id, 'Mesa', 1, 'MUEBLES');

INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES
('Familia Perez', NOW(), 'Retiro final y cierre', 1, 20, 'Se recogió todo', 1, 1);
SET @visita3_id = LAST_INSERT_ID();
INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES
(@visita3_id, 'Cama', 2, 'MUEBLES'),
(@visita3_id, 'Caja de alimentos', 10, 'ALIMENTOS');

-- End of script