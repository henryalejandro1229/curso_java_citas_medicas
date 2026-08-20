package com.henry.pacientes.service;

import com.henry.commons.dto.pacientes.PacienteRequest;
import com.henry.commons.dto.pacientes.PacienteResponse;
import com.henry.commons.service.CrudService;

import java.util.List;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {

    PacienteResponse obtenerPacientePorId(Long id);

}
