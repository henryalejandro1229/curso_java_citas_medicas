package com.henry.pacientes.service;

import com.henry.commons.dto.pacientes.PacienteRequest;
import com.henry.commons.dto.pacientes.PacienteResponse;
import com.henry.commons.enums.EstadoRegistro;
import com.henry.commons.exceptions.RecursoNoEncontradoException;
import com.henry.pacientes.entity.Paciente;
import com.henry.pacientes.mapper.PacienteMapper;
import com.henry.pacientes.repository.PacienteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    private final PacienteMapper pacienteMapper;

    @Override
    public List<PacienteResponse> listar() {
        log.info("Listando todos los pacientes");

        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    public PacienteResponse obtenerPacienteActivoPorId(Long id) {
        log.info("Listando paciente activo con id: {}", id);
        return pacienteMapper.entidadAResponse(obtenerPacienteActivo(id));
    }

    @Override
    public PacienteResponse obtenerPorId(Long id) {
        log.info("Listando paciente con id: {}", id);
        return pacienteMapper.entidadAResponse(
                pacienteRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException("Paciente no encontrado con id: " + id)));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando paciente ...");

        Paciente paciente = pacienteMapper.requestAEntidad(request);

        paciente.generarNumExpediente();

        paciente.calcularIMC();

        pacienteRepository.save(paciente);

        log.info("Nuevo paciente registrado correctamente");

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, long id) {
        Paciente paciente = obtenerPacienteActivo(id);

        log.info("Actualizando datos de paciente con id: ", id);

        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion()
        );

        log.info("Nuevo paciente actualizado correctamente");

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPacienteActivo(id);

        log.info("Eliminando paciente con id: {}", id);

        paciente.setEstatusEliminado();

        pacienteRepository.save(paciente);

        log.info("Paciente con id {} eliminado correctamente", paciente.getId());
    }

    private Paciente obtenerPacienteActivo(Long id) {
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Paciente activo no encontrado con id: " + id));
    }
}