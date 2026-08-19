package com.henry.pacientes.service;

import com.henry.commons.dto.pacientes.PacienteRequest;
import com.henry.commons.dto.pacientes.PacienteResponse;
import com.henry.commons.service.CrudService;

import java.util.List;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {

    //List<PacienteResponse> listar();

    PacienteResponse obtenerPacienteActivoPorId(Long id);

    //PacienteResponse obtenerPacientePorId(Long id);

    //PacienteResponse registrar(PacienteRequest request);

    //PacienteResponse actualizar(PacienteRequest request, Long id);

    //void eliminar(Long id);

}
