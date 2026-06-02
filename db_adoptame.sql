CREATE DATABASE IF NOT EXISTS sistema_adopcion;
USE sistema_adopcion;

-- 1. Tabla: Usuario
CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellidoPaterno VARCHAR(100) NOT NULL,
    apellidoMaterno VARCHAR(100),
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    activo TINYINT(1) DEFAULT 1,
    fechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idUsuario)
) ENGINE=InnoDB;

-- 2. Tabla: CatTipoMascota
CREATE TABLE CatTipoMascota (
    idTipoMascota INT AUTO_INCREMENT,
    descripcion VARCHAR(50) NOT NULL,
    activo TINYINT(1) DEFAULT 1,
    PRIMARY KEY (idTipoMascota)
) ENGINE=InnoDB;

-- 3. Tabla: Mascota
CREATE TABLE Mascota (
    idMascota INT AUTO_INCREMENT,
    idUsuarioDonador INT NOT NULL,
    idTipoMascota INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    raza VARCHAR(100),
    sexo ENUM('MACHO', 'HEMBRA') NOT NULL,
    edadAproximada VARCHAR(50),
    descripcion TEXT,
    estadoAdopcion ENUM('DISPONIBLE', 'PROCESO', 'ADOPTADO') NOT NULL,
    activo TINYINT(1) DEFAULT 1,
    fechaPublicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idMascota),
    CONSTRAINT fk_mascota_usuario FOREIGN KEY (idUsuarioDonador) 
        REFERENCES Usuario(idUsuario) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_mascota_tipo FOREIGN KEY (idTipoMascota) 
        REFERENCES CatTipoMascota(idTipoMascota) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 4. Tabla: ImagenMascota
CREATE TABLE ImagenMascota (
    idImagen INT AUTO_INCREMENT,
    idMascota INT NOT NULL,
    urlImagen VARCHAR(255) NOT NULL,
    imagenPrincipal TINYINT(1) DEFAULT 0,
    fechaSubida DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idImagen),
    CONSTRAINT fk_imagen_mascota FOREIGN KEY (idMascota) 
        REFERENCES Mascota(idMascota) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 5. Tabla: SolicitudAdopcion
CREATE TABLE SolicitudAdopcion (
    idSolicitud INT AUTO_INCREMENT,
    idMascota INT NOT NULL,
    idUsuarioSolicitante INT NOT NULL,
    mensaje TEXT,
    estadoSolicitud ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA') NOT NULL,
    fechaSolicitud DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (idSolicitud),
    CONSTRAINT fk_solicitud_mascota FOREIGN KEY (idMascota) 
        REFERENCES Mascota(idMascota) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_solicitud_usuario FOREIGN KEY (idUsuarioSolicitante) 
        REFERENCES Usuario(idUsuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 6. Tabla: Adopcion
CREATE TABLE Adopcion (
    idAdopcion INT AUTO_INCREMENT,
    idSolicitud INT NOT NULL,
    idMascota INT NOT NULL,
    idUsuarioAdoptante INT NOT NULL,
    fechaAdopcion DATETIME DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    PRIMARY KEY (idAdopcion),
    CONSTRAINT fk_adopcion_solicitud FOREIGN KEY (idSolicitud) 
        REFERENCES SolicitudAdopcion(idSolicitud) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_adopcion_mascota FOREIGN KEY (idMascota) 
        REFERENCES Mascota(idMascota) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_adopcion_usuario FOREIGN KEY (idUsuarioAdoptante) 
        REFERENCES Usuario(idUsuario) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;


USE sistema_adopcion;

-- Inserciones para CatTipoMascota
INSERT INTO CatTipoMascota (descripcion) VALUES
('Perro'),
('Gato'),
('Ave'),
('Roedor');