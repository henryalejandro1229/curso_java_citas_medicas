package com.henry.citas.service;

import com.henry.citas.dto.CitaRequest;
import com.henry.citas.dto.CitaResponse;
import com.henry.citas.entity.Cita;
import com.henry.citas.enums.EstadoCita;
import com.henry.citas.mapper.CitaMapper;
import com.henry.citas.repository.CitaRepository;
import com.henry.commons.client.MedicoClient;
import com.henry.commons.client.PacienteClient;
import com.henry.commons.dto.medicos.MedicoResponse;
import com.henry.commons.dto.pacientes.PacienteResponse;
import com.henry.commons.enums.EstadoRegistro;
import com.henry.commons.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class CitaServiceImpl implements CitaService{

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;

    private final PacienteClient pacienteClient;

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas");

        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {

        Cita cita = obtenerCitaOExcepcion(id);

        return citaMapper.entidadAResponse(
                cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico())
        );
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {

        log.info("Registrando nueva cita...");

        MedicoResponse medicoResponse = obtenerMedicoActivo(request.idMedico());

        PacienteResponse pacienteResponse = obtenerPacienteActivo(request.idPaciente());

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.save(cita);

        log.info("Cita registrada exitosamente");

        return citaMapper.entidadAResponse(
                cita,
                pacienteResponse,
                medicoResponse
        );
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, long id) {

        log.info("Actualizando cita con id :" + id);

        Cita cita = obtenerCitaOExcepcion(id);

        MedicoResponse medicoResponse = obtenerMedicoActivo(request.idMedico());

        PacienteResponse pacienteResponse = obtenerPacienteActivo(request.idPaciente());

        log.info("Actualizando cita con id: {}", id);

        //TODO Validar cuando cambia el médico
        //TODO Validar el estado de la cita
        //TODO Implementar y Validar el paciente

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        log.info("Cita actualizada con el id: {}", id);

        return citaMapper.entidadAResponse(
                cita,
                pacienteResponse,
                medicoResponse
        );
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        Cita cita = obtenerCitaOExcepcion(idCita);

        //TODO Actualizar estatud del medico si se finaliza el estado de una cita

        log.info("Actualizando estado de la cita con id: " + idCita );

        cita.actualizarEstadoCita(EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita));

        log.info("Estado de la cita {} actualizado correctamente", idCita);
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOExcepcion(id);

        log.info("Eliminando cita con id: " + id);

        cita.eliminar();

        log.info("Cita con id {} ha sido marcada como eliminada", id);
    }

    private Cita obtenerCitaOExcepcion(Long id) {

        log.info("Buscando cita con id: {}", id);

        return citaRepository.findById(id).orElseThrow(()->
            new RecursoNoEncontradoException("Cita no encontrada on id: " + id));
    }

    private PacienteResponse obtenerPacienteActivo(Long id) {
        log.info("Buscando paciente activo con id {} en el servicio remoto...", id);

        return pacienteClient.obtenerPacienteActivoPorId(id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id) {
        log.info("Buscando paciente sin estado con id {} en el servicio remoto...", id);

        return pacienteClient.obtenerPacientesPorIdSinEstado(id);
    }

    private MedicoResponse obtenerMedicoActivo(Long id) {
        log.info("Buscando médico activo con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id) {
        log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);

        return medicoClient.obtenerMedicoPorIdSinEstado(id);
    }
}
