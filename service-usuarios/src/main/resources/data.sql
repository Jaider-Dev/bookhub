-- Crear usuarios (10 usuarios con nombres colombianos)
-- Nota: La contraseña hasheada es "password123"
INSERT IGNORE INTO usuarios (nombre, email, telefono, cedula, contrasena, activo, rol) VALUES
('María González', 'maria.gonzalez@bookhub.co', '3001234567', '1234567890', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Carlos Rodríguez', 'carlos.rodriguez@bookhub.co', '3002345678', '2345678901', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Ana Martínez', 'ana.martinez@bookhub.co', '3003456789', '3456789012', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Luis Hernández', 'luis.hernandez@bookhub.co', '3004567890', '4567890123', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Sofia López', 'sofia.lopez@bookhub.co', '3005678901', '5678901234', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Diego Pérez', 'diego.perez@bookhub.co', '3006789012', '6789012345', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Valentina Sánchez', 'valentina.sanchez@bookhub.co', '3007890123', '7890123456', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Andrés Ramírez', 'andres.ramirez@bookhub.co', '3008901234', '8901234567', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Camila Torres', 'camila.torres@bookhub.co', '3009012345', '9012345678', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR'),
('Sebastián Gómez', 'sebastian.gomez@bookhub.co', '3000123456', '0123456789', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, 'LECTOR');
