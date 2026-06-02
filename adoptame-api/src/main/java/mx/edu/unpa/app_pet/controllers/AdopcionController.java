package mx.edu.unpa.app_pet.controllers;

import mx.edu.unpa.app_pet.dtos.request.AdopcionRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.AdopcionResponseDTO;
import mx.edu.unpa.app_pet.services.AdopcionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adopciones")
@CrossOrigin(origins = "*")
public class AdopcionController {

    private final AdopcionService adopcionService;

    public AdopcionController(AdopcionService adopcionService) {
        this.adopcionService = adopcionService;
    }

    @PostMapping
    public ResponseEntity<AdopcionResponseDTO> registrarAdopcion(@RequestBody AdopcionRequestDTO requestDTO) {
        AdopcionResponseDTO nuevaAdopcion = adopcionService.concretarAdopcion(requestDTO);
        return new ResponseEntity<>(nuevaAdopcion, HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AdopcionResponseDTO>> obtenerMisAdopciones(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(adopcionService.obtenerAdopcionesPorUsuario(idUsuario));
    }
}
