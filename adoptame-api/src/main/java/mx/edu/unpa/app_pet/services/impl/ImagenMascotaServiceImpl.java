package mx.edu.unpa.app_pet.services.impl;

import mx.edu.unpa.app_pet.domains.ImagenMascota;
import mx.edu.unpa.app_pet.dtos.response.ImagenMascotaResponseDTO;
import mx.edu.unpa.app_pet.mappers.ImagenMascotaMapper;
import mx.edu.unpa.app_pet.repository.ImagenMascotaRepository;
import mx.edu.unpa.app_pet.services.FileStorageService;
import mx.edu.unpa.app_pet.services.ImagenMascotaService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImagenMascotaServiceImpl implements ImagenMascotaService {

    private final ImagenMascotaRepository imagenRepository;
    private final ImagenMascotaMapper imagenMapper;
    private final FileStorageService fileStorageService;

    public ImagenMascotaServiceImpl(ImagenMascotaRepository imagenRepository,
                                    ImagenMascotaMapper imagenMapper,
                                    FileStorageService fileStorageService) {
        this.imagenRepository = imagenRepository;
        this.imagenMapper = imagenMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ImagenMascotaResponseDTO subirImagenMascota(Integer idMascota, Boolean esPrincipal, MultipartFile archivo) {
        // 1. Guardar el archivo físico y obtener la ruta
        String urlGenerada = fileStorageService.guardarImagen(archivo);

        // 2. Crear la entidad para la BD
        ImagenMascota nuevaImagen = new ImagenMascota();
        nuevaImagen.setIdMascota(idMascota);
        nuevaImagen.setUrlImagen(urlGenerada);
        // Si no se envía el parámetro, por defecto es false
        nuevaImagen.setImagenPrincipal(esPrincipal != null ? esPrincipal : false);

        // 3. Guardar en la BD
        ImagenMascota imagenGuardada = imagenRepository.save(nuevaImagen);

        // 4. Retornar el ResponseDTO
        return imagenMapper.toResponseDto(imagenGuardada);
    }

    @Override
    public List<ImagenMascotaResponseDTO> obtenerImagenesPorMascota(Integer idMascota) {
        List<ImagenMascota> imagenes = imagenRepository.findByIdMascota(idMascota);
        return imagenMapper.toResponseDtoList(imagenes);
    }
}
