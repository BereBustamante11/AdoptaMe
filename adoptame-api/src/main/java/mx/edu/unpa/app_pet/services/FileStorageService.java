package mx.edu.unpa.app_pet.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Ruta donde se guardarán las imágenes físicas
    private final String uploadDir = "src/main/resources/static/imagenes/";

    public String guardarImagen(MultipartFile archivo) {
        try {
            Path directorio = Paths.get(uploadDir);
            if (!Files.exists(directorio)) {
                Files.createDirectories(directorio);
            }

            // Generamos un nombre único para evitar que las fotos se sobrescriban
            String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path rutaDestino = directorio.resolve(nombreArchivo);

            Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            // Retornamos la URL relativa que la app móvil usará para cargar la imagen
            return "/imagenes/" + nombreArchivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen: " + e.getMessage());
        }
    }
}
