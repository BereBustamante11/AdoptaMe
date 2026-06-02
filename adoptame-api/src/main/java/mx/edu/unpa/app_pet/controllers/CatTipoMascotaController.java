package mx.edu.unpa.app_pet.controllers;

import mx.edu.unpa.app_pet.dtos.request.CatTipoMascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.CatTipoMascotaResponseDTO;
import mx.edu.unpa.app_pet.services.CatTipoMascotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-mascota")
@CrossOrigin(origins = "*")
public class CatTipoMascotaController {

    private final CatTipoMascotaService tipoMascotaService;

    public CatTipoMascotaController(CatTipoMascotaService tipoMascotaService) {
        this.tipoMascotaService = tipoMascotaService;
    }

    @PostMapping
    public ResponseEntity<CatTipoMascotaResponseDTO> crearTipo(@RequestBody CatTipoMascotaRequestDTO requestDTO) {
        CatTipoMascotaResponseDTO nuevoTipo = tipoMascotaService.crearTipoMascota(requestDTO);
        return new ResponseEntity<>(nuevoTipo, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CatTipoMascotaResponseDTO>> obtenerTipos() {
        return ResponseEntity.ok(tipoMascotaService.obtenerTiposActivos());
    }
}
