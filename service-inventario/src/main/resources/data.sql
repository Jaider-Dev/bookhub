-- Crear autores
INSERT IGNORE INTO autor (id, nombre, nacionalidad) VALUES
(1, 'Gabriel García Márquez', 'Colombiana'),
(2, 'Álvaro Mutis', 'Colombiana'),
(3, 'Laura Restrepo', 'Colombiana'),
(4, 'Héctor Abad Faciolince', 'Colombiana'),
(5, 'Juan Gabriel Vásquez', 'Colombiana'),
(6, 'Piedad Bonnett', 'Colombiana'),
(7, 'Santiago Gamboa', 'Colombiana'),
(8, 'Mario Mendoza', 'Colombiana'),
(9, 'William Ospina', 'Colombiana'),
(10, 'Fernando Vallejo', 'Colombiana');

-- Crear categorías
INSERT IGNORE INTO categoria (id, nombre) VALUES
(1, 'Literatura Colombiana'),
(2, 'Novela'),
(3, 'Cuento'),
(4, 'Poesía'),
(5, 'Ensayo'),
(6, 'Historia'),
(7, 'Biografía'),
(8, 'Ciencia Ficción'),
(9, 'Misterio'),
(10, 'Romance');

-- Crear libros
INSERT IGNORE INTO libro (id, titulo, isbn, anio_publicacion, autor_id, categoria_id) VALUES
(1, 'Cien años de soledad', '978-84-376-0494-7', 1967, 1, 1),
(2, 'El amor en los tiempos del cólera', '978-84-376-0495-4', 1985, 1, 2),
(3, 'El coronel no tiene quien le escriba', '978-84-376-0496-1', 1961, 1, 3),
(4, 'La vorágine', '978-84-376-0497-8', 1924, 2, 1),
(5, 'Delirio', '978-84-376-0498-5', 2004, 3, 2),
(6, 'El olvido que seremos', '978-84-376-0499-2', 2006, 4, 7),
(7, 'El ruido de las cosas al caer', '978-84-376-0500-5', 2011, 5, 2),
(8, 'Lo que no tiene nombre', '978-84-376-0501-2', 2013, 6, 7),
(9, 'Plegarias nocturnas', '978-84-376-0502-9', 2012, 7, 2),
(10, 'Satanás', '978-84-376-0503-6', 2002, 8, 2);

-- Crear ejemplares para cada libro
INSERT IGNORE INTO ejemplar (libro_id, codigo_inventario, estado) VALUES
-- Cien años de soledad (3 ejemplares)
(1, 'LIB-1-1', 'DISPONIBLE'),
(1, 'LIB-1-2', 'DISPONIBLE'),
(1, 'LIB-1-3', 'DISPONIBLE'),
-- El amor en los tiempos del cólera (2 ejemplares)
(2, 'LIB-2-1', 'DISPONIBLE'),
(2, 'LIB-2-2', 'DISPONIBLE'),
-- El coronel no tiene quien le escriba (2 ejemplares)
(3, 'LIB-3-1', 'DISPONIBLE'),
(3, 'LIB-3-2', 'DISPONIBLE'),
-- La vorágine (2 ejemplares)
(4, 'LIB-4-1', 'DISPONIBLE'),
(4, 'LIB-4-2', 'DISPONIBLE'),
-- Delirio (2 ejemplares)
(5, 'LIB-5-1', 'DISPONIBLE'),
(5, 'LIB-5-2', 'DISPONIBLE'),
-- El olvido que seremos (2 ejemplares)
(6, 'LIB-6-1', 'DISPONIBLE'),
(6, 'LIB-6-2', 'DISPONIBLE'),
-- El ruido de las cosas al caer (2 ejemplares)
(7, 'LIB-7-1', 'DISPONIBLE'),
(7, 'LIB-7-2', 'DISPONIBLE'),
-- Lo que no tiene nombre (1 ejemplar)
(8, 'LIB-8-1', 'DISPONIBLE'),
-- Plegarias nocturnas (2 ejemplares)
(9, 'LIB-9-1', 'DISPONIBLE'),
(9, 'LIB-9-2', 'DISPONIBLE'),
-- Satanás (2 ejemplares)
(10, 'LIB-10-1', 'DISPONIBLE'),
(10, 'LIB-10-2', 'DISPONIBLE');
