-- Configurar codificación
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Crear bases de datos
CREATE DATABASE IF NOT EXISTS bookhub_usuarios;
CREATE DATABASE IF NOT EXISTS bookhub_inventario;
CREATE DATABASE IF NOT EXISTS bookhub_prestamos;

-- -----------------------------------------------------------------------------
-- CONFIGURACIÓN DE USUARIOS
-- -----------------------------------------------------------------------------
USE bookhub_usuarios;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    telefono VARCHAR(20),
    cedula VARCHAR(20),
    contrasena VARCHAR(255),
    activo BIT(1),
    rol VARCHAR(50)
);

INSERT INTO usuarios (nombre, email, telefono, cedula, contrasena, activo, rol) VALUES
('María González', 'maria.gonzalez@bookhub.co', '3001234567', '1234567890', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Carlos Rodríguez', 'carlos.rodriguez@bookhub.co', '3002345678', '2345678901', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Ana Martínez', 'ana.martinez@bookhub.co', '3003456789', '3456789012', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Luis Hernández', 'luis.hernandez@bookhub.co', '3004567890', '4567890123', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Sofia López', 'sofia.lopez@bookhub.co', '3005678901', '5678901234', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Diego Pérez', 'diego.perez@bookhub.co', '3006789012', '6789012345', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Valentina Sánchez', 'valentina.sanchez@bookhub.co', '3007890123', '7890123456', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Andrés Ramírez', 'andres.ramirez@bookhub.co', '3008901234', '8901234567', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Camila Torres', 'camila.torres@bookhub.co', '3009012345', '9012345678', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR'),
('Sebastián Gómez', 'sebastian.gomez@bookhub.co', '3000123456', '0123456789', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, 'LECTOR');

-- -----------------------------------------------------------------------------
-- CONFIGURACIÓN DE INVENTARIO
-- -----------------------------------------------------------------------------
USE bookhub_inventario;

CREATE TABLE IF NOT EXISTS autor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    nacionalidad VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS libro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255),
    isbn VARCHAR(255),
    anio_publicacion INT,
    autor_id BIGINT,
    categoria_id BIGINT,
    FOREIGN KEY (autor_id) REFERENCES autor(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE IF NOT EXISTS ejemplar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libro_id BIGINT,
    codigo_inventario VARCHAR(255),
    estado VARCHAR(50) DEFAULT 'DISPONIBLE',
    FOREIGN KEY (libro_id) REFERENCES libro(id)
);

INSERT INTO autor (nombre, nacionalidad) VALUES
('Gabriel García Márquez', 'Colombiana'),
('Álvaro Mutis', 'Colombiana'),
('Laura Restrepo', 'Colombiana'),
('Héctor Abad Faciolince', 'Colombiana'),
('Juan Gabriel Vásquez', 'Colombiana'),
('Piedad Bonnett', 'Colombiana'),
('Santiago Gamboa', 'Colombiana'),
('Mario Mendoza', 'Colombiana'),
('William Ospina', 'Colombiana'),
('Fernando Vallejo', 'Colombiana');

INSERT INTO categoria (nombre) VALUES
('Literatura Colombiana'),
('Novela'),
('Cuento'),
('Poesía'),
('Ensayo'),
('Historia'),
('Biografía'),
('Ciencia Ficción'),
('Misterio'),
('Romance');

INSERT INTO libro (titulo, isbn, anio_publicacion, autor_id, categoria_id) VALUES
('Cien años de soledad', '978-84-376-0494-7', 1967, 1, 1),
('El amor en los tiempos del cólera', '978-84-376-0495-4', 1985, 1, 2),
('El coronel no tiene quien le escriba', '978-84-376-0496-1', 1961, 1, 3),
('La vorágine', '978-84-376-0497-8', 1924, 2, 1),
('Delirio', '978-84-376-0498-5', 2004, 3, 2),
('El olvido que seremos', '978-84-376-0499-2', 2006, 4, 7),
('El ruido de las cosas al caer', '978-84-376-0500-5', 2011, 5, 2),
('Lo que no tiene nombre', '978-84-376-0501-2', 2013, 6, 7),
('Plegarias nocturnas', '978-84-376-0502-9', 2012, 7, 2),
('Satanás', '978-84-376-0503-6', 2002, 8, 2);

INSERT INTO ejemplar (libro_id, codigo_inventario, estado) VALUES
(1, 'LIB-1-1', 'DISPONIBLE'),
(1, 'LIB-1-2', 'DISPONIBLE'),
(1, 'LIB-1-3', 'DISPONIBLE'),
(2, 'LIB-2-1', 'DISPONIBLE'),
(2, 'LIB-2-2', 'DISPONIBLE'),
(3, 'LIB-3-1', 'DISPONIBLE'),
(3, 'LIB-3-2', 'DISPONIBLE'),
(4, 'LIB-4-1', 'DISPONIBLE'),
(4, 'LIB-4-2', 'DISPONIBLE'),
(5, 'LIB-5-1', 'DISPONIBLE'),
(5, 'LIB-5-2', 'DISPONIBLE'),
(6, 'LIB-6-1', 'DISPONIBLE'),
(6, 'LIB-6-2', 'DISPONIBLE'),
(7, 'LIB-7-1', 'DISPONIBLE'),
(7, 'LIB-7-2', 'DISPONIBLE'),
(8, 'LIB-8-1', 'DISPONIBLE'),
(9, 'LIB-9-1', 'DISPONIBLE'),
(9, 'LIB-9-2', 'DISPONIBLE'),
(10, 'LIB-10-1', 'DISPONIBLE'),
(10, 'LIB-10-2', 'DISPONIBLE');

-- -----------------------------------------------------------------------------
-- CONFIGURACIÓN DE PRESTAMOS
-- -----------------------------------------------------------------------------
USE bookhub_prestamos;

CREATE TABLE IF NOT EXISTS prestamo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    ejemplar_id BIGINT,
    fecha_prestamo DATE,
    fecha_vencimiento DATE,
    fecha_devolucion_real DATE,
    estado VARCHAR(50)
);
