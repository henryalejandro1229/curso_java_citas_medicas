package com.henry.citas.service;

import com.henry.citas.dto.CitaRequest;
import com.henry.citas.dto.CitaResponse;
import com.henry.commons.service.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {

    void actualizarEstadoCita(Long idCita, Long idEstadoCita);

    void consultarCitasActivasPorPaciente(Long idPaciente);

    void consultarCitasActivasPorMedico(Long idMedico);
}
