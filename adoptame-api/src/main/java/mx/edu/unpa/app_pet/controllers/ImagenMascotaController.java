package mx.edu.unpa.app_pet.controllers;

import mx.edu.unpa.app_pet.dtos.response.ImagenMascotaResponseDTO;
import mx.edu.unpa.app_pet.services.ImagenMascotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/imagenes-mascota")
@CrossOrigin(origins = "*")
public class ImagenMascotaController {

    private final ImagenMascotaService imagenMascotaService;

    public ImagenMascotaController(ImagenMascotaService imagenMascotaService) {
        this.imagenMascotaService = imagenMascotaService;
    }

    // Usamos consume = multipart/form-data para ser explícitos
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ImagenMascotaResponseDTO> subirImagen(
            @RequestParam("idMascota") Integer idMascota,
            @RequestParam(value = "imagenPrincipal", required = false) Boolean imagenPrincipal,
            @RequestParam("archivo") MultipartFile archivo) {
        System.out.println("ID recibido: " + idMascota);

        ImagenMascotaResponseDTO respuesta = imagenMascotaService.subirImagenMascota(idMascota, imagenPrincipal, archivo);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<ImagenMascotaResponseDTO>> obtenerImagenes(@PathVariable Integer idMascota) {
        return ResponseEntity.ok(imagenMascotaService.obtenerImagenesPorMascota(idMascota));
    }
}
