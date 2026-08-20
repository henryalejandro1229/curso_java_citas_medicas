package com.henry.medicos.service;

import com.henry.commons.client.CitaClient;
import com.henry.commons.dto.medicos.MedicoRequest;
import com.henry.commons.dto.medicos.MedicoResponse;
import com.henry.commons.enums.DisponibilidadMedico;
import com.henry.commons.enums.EspecialidadMedico;
import com.henry.commons.enums.EstadoRegistro;
import com.henry.commons.exceptions.RecursoNoEncontradoException;
import com.henry.medicos.entity.Medico;
import com.henry.medicos.mapper.MedicoMapper;
import com.henry.medicos.repository.MedicoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepository;

    private final MedicoMapper medicoMapper;

    private final CitaClient citaClient;

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        log.info("Listando todos los médicos activos");

        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        log.info("Listando médico activo con id: " + id);
        return medicoMapper.entidadAResponse(obtenerMedicoActivoOException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando médico sin estado con id: {}" + id);

        return medicoMapper.entidadAResponse(medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Médico sin estado no encontrado con id: " + id)));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrando nuevo médico {}", request.nombre());

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medico.actualizarEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );

        medicoRepository.save(medico);

        log.info("Nuevo médico registrado: {}", medico.getNombre());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, long id) {
        Medico medico = obtenerMedicoActivoOException(id);

        log.info("Actualizando médico con id: {}", id);

        existenCitasActivasPorMedico(id);

        validarCambiosUnicos(request, id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        log.info("Médico actualizado exitosamente");


        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoOException(idMedico);

        log.info("Actualizando disponibilidad del médico con id: {}", idDisponibilidad);

        /*
        Se omite peticion a citas por reglas de negocio
        if (DisponibilidadMedico.DISPONIBLE.getCodigo().equals(idDisponibilidad))
            existenCitasActivasPorMedico(idMedico); */

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico
                .obtenerDisponibilidadPorCodigo(idDisponibilidad);

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        log.info("Disponibilidad del médico con id {} cambió de {} a {}",
                idMedico, disponibilidadAnterior, nuevaDisponibilidad);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoOException(id);

        log.info("Eliminando médico con id: {}", id);

        existenCitasActivasPorMedico(id);

        medico.eliminar();

        log.info("Médico eliminado exitósamente");
    }

    private Medico obtenerMedicoActivoOException(Long id) {
        log.info("Buscando médico activ con id: {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Médico activo no encontrado con id: " + id
                ));
    }

    private void validarDatosUnicos(MedicoRequest request) {
        log.info("Validando email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando teléfono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el teléfono: " + request.telefono());

        log.info("Validando cédula unico...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con la cédula profesional: "
                    + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id) {
        log.info("Validando email único...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el email: " + request.email());

        log.info("Validando teléfono único...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con el teléfono: " + request.telefono());

        log.info("Validando cédula unico...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new IllegalArgumentException("Ya existe un médico activo registrado con la cédula profesional: "
                    + request.cedulaProfesional());
    }

    private void existenCitasActivasPorMedico(Long id) {
        log.info("Verificando citas activas del médico con id {} en el servicio remoto...", id);

        citaClient.existenCitasActivasPorMedico(id);
    }
}
