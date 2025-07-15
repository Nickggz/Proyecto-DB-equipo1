-- ============================================
-- Usar la base de datos que ya fue creada por las variables de entorno
USE mi_proyecto_db;

-- Configurar charset para evitar problemas con caracteres especiales
SET NAMES utf8mb4;
SET character_set_client = utf8mb4;

-- =====================================================
-- 1. TABLA DEPARTAMENTOS
-- =====================================================
CREATE TABLE departamento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    codigo VARCHAR(5) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO departamento (nombre, codigo) VALUES
('Montevideo', 'MON'),
('Canelones', 'CAN'),
('Maldonado', 'MAL');

-- =====================================================
-- 2. TABLA ESTABLECIMIENTOS
-- =====================================================
CREATE TABLE establecimiento (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(200) NOT NULL,
    direccion VARCHAR(300),
    departamento VARCHAR(50),
    capacidad INTEGER,
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO establecimiento (nombre, direccion, departamento, capacidad, activo) VALUES
('Escuela N° 1 - José Pedro Varela', 'Av. 18 de Julio 1234', 'Montevideo', 800, TRUE),
('Liceo N° 5 - Pocitos', 'Bulevar Artigas 567', 'Montevideo', 600, TRUE),
('Escuela Rural N° 45', 'Ruta 8 Km 23', 'Canelones', 400, TRUE);

-- =====================================================
-- 3. TABLA CIRCUITOS
-- =====================================================
CREATE TABLE circuito (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(10) NOT NULL,
    nombre VARCHAR(100),
    departamento VARCHAR(50),
    id_departamento BIGINT,
    id_establecimiento BIGINT,
    rango_credencial_inicio VARCHAR(10),
    rango_credencial_fin VARCHAR(10),
    PRIMARY KEY (id),
    FOREIGN KEY (id_departamento) REFERENCES departamento(id),
    FOREIGN KEY (id_establecimiento) REFERENCES establecimiento(id)
) ENGINE=InnoDB;

INSERT INTO circuito (numero, nombre, departamento, id_departamento, id_establecimiento, rango_credencial_inicio, rango_credencial_fin) VALUES
('MON001', 'Montevideo Centro', 'Montevideo', 1, 1, 'AAA000001', 'AAA999999'),
('MON002', 'Montevideo Pocitos', 'Montevideo', 1, 2, 'AAB000001', 'AAB999999'),
('CAN001', 'Canelones Norte', 'Canelones', 2, 3, 'ABC000001', 'ABC999999');

-- =====================================================
-- 4. TABLA USUARIOS CON ROL COMO ENUM
-- =====================================================
CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cedula VARCHAR(8) NOT NULL UNIQUE,
    credencial_civica VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    fecha_registro DATETIME(6),
    id_circuito BIGINT,
    rol ENUM('VOTANTE', 'PRESIDENTE_MESA', 'SECRETARIO_MESA', 'VOCAL_MESA', 'ADMIN') DEFAULT 'VOTANTE',
    PRIMARY KEY (id),
    FOREIGN KEY (id_circuito) REFERENCES circuito(id)
) ENGINE=InnoDB;

-- Insertar usuarios votantes normales
INSERT INTO usuarios (cedula, credencial_civica, nombre, email, fecha_registro, id_circuito, rol) VALUES
('29236278', 'JDD2952', 'Elba Elena González', 'maria.gonzalez@email.com', NOW(), 2, 'VOTANTE'),
('32853156', 'JBD8491', 'Alejandro Luis Martínez', 'pedro.martinez@email.com', NOW(), 3, 'VOTANTE');

-- Insertar usuarios con roles especiales
INSERT INTO usuarios (cedula, credencial_civica, nombre, email, fecha_registro, id_circuito, rol) VALUES
('54961662', 'ICD26204', 'Ana Marconi Rodríguez - Presidente Mesa 1', 'ana.rodriguez@email.com', NOW(), 1, 'PRESIDENTE_MESA'),
('40234567', 'PRE002345', 'Carlos Fernández - Presidente Mesa 2', 'carlos.fernandez@email.com', NOW(), 2, 'PRESIDENTE_MESA'),
('40345678', 'PRE003456', 'Elena Vásquez - Presidente Mesa 3', 'elena.vasquez@email.com', NOW(), 3, 'PRESIDENTE_MESA'),
('35813561', 'JBC10084', 'Alejandro Silva - Secretario Mesa 1', 'roberto.silva@email.com', NOW(), 1, 'SECRETARIO_MESA'),
('41234567', 'SEC002345', 'Patricia López - Secretario Mesa 2', 'patricia.lopez@email.com', NOW(), 2, 'SECRETARIO_MESA'),
('41345678', 'SEC003456', 'Miguel Torres - Secretario Mesa 3', 'miguel.torres@email.com', NOW(), 3, 'SECRETARIO_MESA'),
('39188982', 'HDD6579', 'Claudia Morales - Vocal Mesa 1', 'lucia.morales@email.com', NOW(), 1, 'VOCAL_MESA'),
('42234567', 'VOC002345', 'Diego Pérez - Vocal Mesa 2', 'diego.perez@email.com', NOW(), 2, 'VOCAL_MESA'),
('42345678', 'VOC003456', 'Carmen Ruiz - Vocal Mesa 3', 'carmen.ruiz@email.com', NOW(), 3, 'VOCAL_MESA'),
('50000000', 'ADM000000', 'Administrador Sistema', 'admin@sistema.com', NOW(), 1, 'ADMIN');

-- =====================================================
-- 5. TABLA MESAS CON CAMPOS DE CONTROL Y MIEMBROS
-- =====================================================
CREATE TABLE mesa (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero VARCHAR(10) NOT NULL,
    id_circuito BIGINT,
    capacidad_votantes INTEGER,
    cerrada BOOLEAN DEFAULT FALSE,
    observaciones TEXT,
    rango_credencial_inicio VARCHAR(10),
    rango_credencial_fin VARCHAR(10),
    fecha_apertura DATETIME(6),
    fecha_cierre DATETIME(6),
    cerrada_por_cedula VARCHAR(8),
    motivo_cierre TEXT,
    total_votos_emitidos INTEGER DEFAULT 0,
    presidente_cedula VARCHAR(8),
    presidente_credencial VARCHAR(20),
    presidente_nombre VARCHAR(100),
    secretario_cedula VARCHAR(8),
    secretario_credencial VARCHAR(20),
    secretario_nombre VARCHAR(100),
    vocal_cedula VARCHAR(8),
    vocal_credencial VARCHAR(20),
    vocal_nombre VARCHAR(100),
    PRIMARY KEY (id),
    FOREIGN KEY (id_circuito) REFERENCES circuito(id),
    INDEX idx_presidente (presidente_cedula, presidente_credencial)
) ENGINE=InnoDB;

INSERT INTO mesa (numero, id_circuito, capacidad_votantes, cerrada, rango_credencial_inicio, rango_credencial_fin,
                  presidente_cedula, presidente_credencial, presidente_nombre,
                  secretario_cedula, secretario_credencial, secretario_nombre,
                  vocal_cedula, vocal_credencial, vocal_nombre) VALUES
('001A', 1, 300, FALSE, 'AAA000001', 'AAA333333',
 '51042982', 'AAA123456', 'Ana María Rodríguez',
 '41123456', 'SEC001234', 'Roberto Silva',
 '42123456', 'VOC001234', 'Lucía Morales'),
('001B', 2, 300, FALSE, 'AAB000001', 'AAB333333',
 '40234567', 'PRE002345', 'Carlos Fernández',
 '41234567', 'SEC002345', 'Patricia López',
 '42234567', 'VOC002345', 'Diego Pérez'),
('001C', 3, 300, FALSE, 'ABC000001', 'ABC333333',
 '40345678', 'PRE003456', 'Elena Vásquez',
 '41345678', 'SEC003456', 'Miguel Torres',
 '42345678', 'VOC003456', 'Carmen Ruiz');

-- =====================================================
-- 6. TABLA PARTIDOS POLÍTICOS
-- =====================================================
CREATE TABLE partido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    siglas VARCHAR(10),
    color VARCHAR(7),
    fecha_fundacion DATE,
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO partido (nombre, siglas, color, fecha_fundacion, activo) VALUES
('Frente Amplio', 'FA', '#FF6B6B', '1971-02-05', TRUE),
('Partido Nacional', 'PN', '#4ECDC4', '1836-06-25', TRUE),
('Partido Colorado', 'PC', '#45B7D1', '1836-06-25', TRUE);

-- =====================================================
-- 7. TABLA ELECCIONES
-- =====================================================
CREATE TABLE eleccion (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200),
    activa BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO eleccion (fecha, tipo, descripcion, activa) VALUES
('2024-10-27', 'PRESIDENCIAL', 'Elecciones Nacionales 2024 - Primera Vuelta', TRUE),
('2024-11-24', 'PRESIDENCIAL', 'Elecciones Nacionales 2024 - Balotaje', FALSE),
('2025-05-10', 'DEPARTAMENTAL', 'Elecciones Departamentales 2025', FALSE);

-- =====================================================
-- 8. TABLA LISTAS
-- =====================================================
CREATE TABLE lista (
    id BIGINT NOT NULL AUTO_INCREMENT,
    numero INTEGER NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    id_partido BIGINT NOT NULL,
    id_eleccion BIGINT NOT NULL,
    activa BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id),
    FOREIGN KEY (id_partido) REFERENCES partido(id),
    FOREIGN KEY (id_eleccion) REFERENCES eleccion(id)
) ENGINE=InnoDB;

INSERT INTO lista (numero, nombre, id_partido, id_eleccion, activa) VALUES
(1, 'Lista 1 - Frente Amplio', 1, 1, TRUE),
(71, 'Lista 71 - Partido Nacional', 2, 1, TRUE),
(15, 'Lista 15 - Partido Colorado', 3, 1, TRUE);

-- =====================================================
-- 9. TABLA TIPOS DE VOTO
-- =====================================================
CREATE TABLE tipo_voto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tipo VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(100),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO tipo_voto (tipo, descripcion) VALUES
('VALIDO', 'Voto válido en circuito correspondiente'),
('OBSERVADO', 'Voto fuera del circuito asignado'),
('ANULADO', 'Voto anulado por irregularidades');

-- =====================================================
-- 10. TABLA CONTROL DE VOTO
-- =====================================================
CREATE TABLE control_voto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_ciudadano BIGINT NOT NULL,
    id_eleccion BIGINT NOT NULL,
    id_mesa BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY UK_control_voto (id_ciudadano, id_eleccion),
    FOREIGN KEY (id_ciudadano) REFERENCES usuarios(id),
    FOREIGN KEY (id_eleccion) REFERENCES eleccion(id),
    FOREIGN KEY (id_mesa) REFERENCES mesa(id)
) ENGINE=InnoDB;

-- =====================================================
-- 11. TABLA VOTOS CON TIPO DE VOTO
-- =====================================================
CREATE TABLE voto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_eleccion BIGINT NOT NULL,
    id_lista BIGINT,
    id_mesa BIGINT NOT NULL,
    fecha_hora DATETIME(6),
    voto_en_blanco BOOLEAN DEFAULT FALSE,
    tipo_voto_id BIGINT DEFAULT 1,
    observaciones TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (id_eleccion) REFERENCES eleccion(id),
    FOREIGN KEY (id_lista) REFERENCES lista(id),
    FOREIGN KEY (id_mesa) REFERENCES mesa(id),
    FOREIGN KEY (tipo_voto_id) REFERENCES tipo_voto(id)
) ENGINE=InnoDB;

-- =====================================================
-- 12. FUNCIÓN PARA DETERMINAR CIRCUITO POR CREDENCIAL
-- =====================================================
DELIMITER //

CREATE FUNCTION obtener_circuito_por_credencial(credencial VARCHAR(20)) 
RETURNS BIGINT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE circuito_id BIGINT DEFAULT NULL;
    
    -- Determinar circuito basado en el prefijo de la credencial
    CASE 
        WHEN credencial LIKE 'AAA%' THEN SET circuito_id = 1; -- Montevideo Centro
        WHEN credencial LIKE 'AAB%' THEN SET circuito_id = 2; -- Montevideo Pocitos  
        WHEN credencial LIKE 'ABC%' THEN SET circuito_id = 3; -- Canelones Norte
        ELSE SET circuito_id = 1; -- Default a Montevideo Centro
    END CASE;
    
    RETURN circuito_id;
END //

DELIMITER ;

-- =====================================================
-- 13. FUNCIÓN PARA DETERMINAR MESA POR CREDENCIAL
-- =====================================================
DELIMITER //

CREATE FUNCTION obtener_mesa_por_credencial(credencial VARCHAR(20)) 
RETURNS BIGINT
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE mesa_id BIGINT DEFAULT NULL;
    
    -- Determinar mesa basada en el prefijo de la credencial
    CASE 
        WHEN credencial LIKE 'AAA%' THEN SET mesa_id = 1; -- Mesa 001A
        WHEN credencial LIKE 'AAB%' THEN SET mesa_id = 2; -- Mesa 001B
        WHEN credencial LIKE 'ABC%' THEN SET mesa_id = 3; -- Mesa 001C
        ELSE SET mesa_id = 1; -- Default
    END CASE;
    
    RETURN mesa_id;
END //

DELIMITER ;

-- =====================================================
-- 14. FUNCIÓN PARA VERIFICAR SI MESA ESTÁ ABIERTA
-- =====================================================
DELIMITER //

CREATE FUNCTION mesa_esta_abierta(p_mesa_id BIGINT) 
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_cerrada BOOLEAN DEFAULT TRUE;
    
    SELECT NOT cerrada INTO v_cerrada FROM mesa WHERE id = p_mesa_id;
    
    RETURN COALESCE(v_cerrada, FALSE);
END //

DELIMITER ;

-- =====================================================
-- 15. PROCEDIMIENTO PARA CERRAR MESA
-- =====================================================
DELIMITER //

CREATE PROCEDURE cerrar_mesa(
    IN p_mesa_id BIGINT,
    IN p_cedula_presidente VARCHAR(8),
    IN p_motivo TEXT,
    OUT p_exito BOOLEAN,
    OUT p_mensaje VARCHAR(300)
)
BEGIN
    DECLARE v_es_presidente BOOLEAN DEFAULT FALSE;
    DECLARE v_ya_cerrada BOOLEAN DEFAULT FALSE;
    DECLARE v_total_votos INT DEFAULT 0;
    
    -- Verificar si ya está cerrada
    SELECT cerrada INTO v_ya_cerrada FROM mesa WHERE id = p_mesa_id;
    
    IF v_ya_cerrada THEN
        SET p_exito = FALSE;
        SET p_mensaje = 'La mesa ya está cerrada';
    ELSE
        -- Verificar si es el presidente de esta mesa
        SELECT COUNT(*) > 0 INTO v_es_presidente 
        FROM mesa 
        WHERE id = p_mesa_id AND presidente_cedula = p_cedula_presidente;
        
        IF NOT v_es_presidente THEN
            SET p_exito = FALSE;
            SET p_mensaje = 'Solo el presidente de mesa puede cerrar la votación';
        ELSE
            -- Contar votos emitidos en esta mesa
            SELECT COUNT(*) INTO v_total_votos 
            FROM voto 
            WHERE id_mesa = p_mesa_id;
            
            -- Cerrar la mesa
            UPDATE mesa SET 
                cerrada = TRUE,
                fecha_cierre = NOW(),
                cerrada_por_cedula = p_cedula_presidente,
                motivo_cierre = p_motivo,
                total_votos_emitidos = v_total_votos
            WHERE id = p_mesa_id;
            
            SET p_exito = TRUE;
            SET p_mensaje = CONCAT('Mesa cerrada exitosamente. Total de votos: ', v_total_votos);
        END IF;
    END IF;
END //

DELIMITER ;

-- =====================================================
-- 16. VISTA PARA INFORMACIÓN COMPLETA DE USUARIOS CON ROLES
-- =====================================================
CREATE VIEW vista_usuarios_completa AS
SELECT 
    u.id,
    u.cedula,
    u.credencial_civica,
    u.nombre,
    u.email,
    u.fecha_registro,
    u.rol,
    c.numero as circuito_numero,
    c.nombre as circuito_nombre,
    d.nombre as departamento,
    e.nombre as establecimiento,
    m.numero as mesa_numero
FROM usuarios u
LEFT JOIN circuito c ON u.id_circuito = c.id
LEFT JOIN departamento d ON c.id_departamento = d.id
LEFT JOIN establecimiento e ON c.id_establecimiento = e.id
LEFT JOIN mesa m ON m.id_circuito = c.id 
    AND u.credencial_civica BETWEEN m.rango_credencial_inicio AND m.rango_credencial_fin;

-- =====================================================
-- 17. VISTA DE ESTADO DE MESAS
-- =====================================================
CREATE VIEW vista_estado_mesas AS
SELECT 
    m.id,
    m.numero,
    m.cerrada,
    m.total_votos_emitidos,
    m.fecha_apertura,
    m.fecha_cierre,
    m.motivo_cierre,
    c.nombre as circuito_nombre,
    c.departamento,
    e.nombre as establecimiento_nombre,
    m.presidente_nombre,
    m.presidente_cedula,
    CASE 
        WHEN m.cerrada THEN 'CERRADA'
        ELSE 'ABIERTA'
    END as estado
FROM mesa m
LEFT JOIN circuito c ON m.id_circuito = c.id
LEFT JOIN establecimiento e ON c.id_establecimiento = e.id;