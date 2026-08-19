package com.henry.medicos.service;

import com.henry.commons.dto.medicos.MedicoRequest;
import com.henry.commons.dto.medicos.MedicoResponse;
import com.henry.commons.service.CrudService;
import org.springframework.stereotype.Service;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {

    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
