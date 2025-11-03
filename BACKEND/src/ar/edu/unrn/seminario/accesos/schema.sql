-- Schema for seminario
-- Run in MySQL to create DB and tables

CREATE DATABASE IF NOT EXISTS seminario DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE seminario;

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

-- pedidos table
CREATE TABLE IF NOT EXISTS pedidos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  descripcion TEXT,
  observaciones TEXT,
  necesitaVehiculo BOOLEAN DEFAULT FALSE,
  donante_username VARCHAR(100),
  fecha_creacion DATETIME,
  activo BOOLEAN DEFAULT TRUE,
  puntaje_total INT DEFAULT 0,
  FOREIGN KEY (donante_username) REFERENCES usuarios(usuario)
);

-- donaciones for pedidos
CREATE TABLE IF NOT EXISTS donaciones (
  id INT PRIMARY KEY AUTO_INCREMENT,
  pedido_id INT,
  tipoDonacion VARCHAR(100),
  categoria VARCHAR(100),
  puntaje INT,
  FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

-- ordenes de retiro
CREATE TABLE IF NOT EXISTS ordenes_retiro (
  id INT PRIMARY KEY AUTO_INCREMENT,
  pedido_id INT,
  voluntario_username VARCHAR(100),
  fecha_generacion DATETIME,
  estado VARCHAR(50),
  FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
  FOREIGN KEY (voluntario_username) REFERENCES usuarios(usuario)
);

-- visitas (linked to ordenes_retiro)
CREATE TABLE IF NOT EXISTS visitas (
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

-- articulos recogidos en cada visita
CREATE TABLE IF NOT EXISTS articulos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  visita_id INT,
  nombre VARCHAR(255),
  cantidad INT,
  tipoDonacion VARCHAR(100),
  FOREIGN KEY (visita_id) REFERENCES visitas(id) ON DELETE CASCADE
);

-- sample data
INSERT IGNORE INTO roles (codigo, nombre, activo) VALUES
(1, 'ADMIN', 1),
(2, 'ESTUDIANTE', 1),
(3, 'INVITADO', 1),
(4, 'DONANTE', 1),
(5, 'VOLUNTARIO', 1);

INSERT IGNORE INTO usuarios (usuario, contrasena, nombre, email, rol_codigo, activo) VALUES
('admin', '1234', 'Admin', 'admin@unrn.edu.ar', 1, 1),
('ldifabio', '4', 'Lucas', 'ldifabio@unrn.edu.ar', 2, 1),
('bjgorosito', '1234', 'Bruno', 'bjgorosito@unrn.edu.ar', 3, 1),
('pgalindo', '5678', 'Pablo', 'pablogalindo90@gmail.com', 4, 1),
('mvoluntario', 'v123', 'Voluntario Uno', 'vol1@example.com', 5, 1),
('donante1', 'd123', 'Donante Uno', 'don1@example.com', 4, 1),
('vol2', 'v456', 'Voluntario Dos', 'vol2@example.com', 5, 1);

INSERT INTO pedidos (descripcion, observaciones, necesitaVehiculo, donante_username, fecha_creacion, activo, puntaje_total) VALUES
('Pedido de ropa y alimentos', 'Ropa usada en buen estado, alimentos no perecederos', 1, 'pgalindo', NOW(), 1, 30),
('Pedido de muebles', 'Camas, mesas y sillas en buen estado', 1, 'donante1', NOW(), 1, 50);

INSERT INTO donaciones (pedido_id, tipoDonacion, categoria, puntaje) VALUES
(LAST_INSERT_ID()-1, 'ROPA', 'INDUMENTARIA', 10),
(LAST_INSERT_ID()-1, 'ALIMENTO', 'NO_PERECEDERO', 20),
(LAST_INSERT_ID(), 'MUEBLE', 'HOGAR', 30),
(LAST_INSERT_ID(), 'MUEBLE', 'HOGAR', 20);

INSERT INTO ordenes_retiro (pedido_id, voluntario_username, fecha_generacion, estado) VALUES
(1, 'mvoluntario', NOW(), 'PENDIENTE'),
(2, 'vol2', NOW(), 'PENDIENTE');

INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES
('Familia Perez', NOW(), 'Entrega de ropa', 1, 10, 'Todo embalado', 1, 0);
INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES
(LAST_INSERT_ID(), 'Camisa', 5, 'ROPA'),
(LAST_INSERT_ID(), 'Pantalon', 3, 'ROPA');

INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES
('Casa Lopez', NOW(), 'Retiro de muebles', 0, 0, 'Programar visita', 2, 0);
INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES
(LAST_INSERT_ID(), 'Mesa', 1, 'MUEBLE');

INSERT INTO visitas (visitante, fechaHora, motivo, confirmada, cantidadBienesRecogidos, observaciones, orden_retiro_id, visitaFinal) VALUES
('Familia Perez', NOW(), 'Retiro final y cierre', 1, 20, 'Se recogió todo', 1, 1);
INSERT INTO articulos (visita_id, nombre, cantidad, tipoDonacion) VALUES
(LAST_INSERT_ID(), 'Cama', 2, 'MUEBLE'),
(LAST_INSERT_ID(), 'Caja de alimentos', 10, 'ALIMENTO');

-- End of schema additions