package mx.edu.unpa.app_pet.services.impl;

import mx.edu.unpa.app_pet.domains.CatTipoMascota;
import mx.edu.unpa.app_pet.dtos.request.CatTipoMascotaRequestDTO;
import mx.edu.unpa.app_pet.dtos.response.CatTipoMascotaResponseDTO;
import mx.edu.unpa.app_pet.mappers.CatTipoMascotaMapper;
import mx.edu.unpa.app_pet.repository.CatTipoMascotaRepository;
import mx.edu.unpa.app_pet.services.CatTipoMascotaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatTipoMascotaServiceImpl implements CatTipoMascotaService {

    private final CatTipoMascotaRepository tipoMascotaRepository;
    private final CatTipoMascotaMapper tipoMascotaMapper;

    public CatTipoMascotaServiceImpl(CatTipoMascotaRepository tipoMascotaRepository, CatTipoMascotaMapper tipoMascotaMapper) {
        this.tipoMascotaRepository = tipoMascotaRepository;
        this.tipoMascotaMapper = tipoMascotaMapper;
    }

    @Override
    public CatTipoMascotaResponseDTO crearTipoMascota(CatTipoMascotaRequestDTO requestDTO) {
        CatTipoMascota tipoMascota = tipoMascotaMapper.toEntity(requestDTO);
        tipoMascota.setActivo(true);
        CatTipoMascota guardado = tipoMascotaRepository.save(tipoMascota);
        return tipoMascotaMapper.toResponseDto(guardado);
    }

    @Override
    public List<CatTipoMascotaResponseDTO> obtenerTiposActivos() {
        List<CatTipoMascota> tiposActivos = tipoMascotaRepository.findByActivoTrue();
        return tipoMascotaMapper.toResponseDtoList(tiposActivos);
    }
}
