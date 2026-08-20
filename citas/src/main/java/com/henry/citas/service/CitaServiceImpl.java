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
import com.henry.commons.enums.DisponibilidadMedico;
import com.henry.commons.enums.EstadoRegistro;
import com.henry.commons.exceptions.RecursoNoEncontradoException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CitaServiceImpl implements CitaService{

    private final CitaRepository citaRepository;

    private final CitaMapper citaMapper;

    private final MedicoClient medicoClient;

    private final PacienteClient pacienteClient;

    private final List<EstadoCita> ESTADOS_CITA_ACTIVOS = List.of(
            EstadoCita.EN_CURSO, EstadoCita.CONFIRMADA, EstadoCita.PENDIENTE);

    private final List<EstadoCita> ESTADOS_CITA = List.of(
            EstadoCita.EN_CURSO, EstadoCita.CONFIRMADA);

    private final Long CODIGO_DISPONIBLE = DisponibilidadMedico.DISPONIBLE.getCodigo();

    private final Long CODIGO_NO_DISPONIBLE = DisponibilidadMedico.NO_DISPONIBLE.getCodigo();

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

        //Valida que el médico exista y esté activo
        MedicoResponse medicoResponse = obtenerMedicoActivo(request.idMedico());

        //Toma idDisponibilidad para saber si corresponde a estatus disponible
        validarEstatusDisponibleMedico(medicoResponse.idDisponibilidad());

        //Valida que el paciente exista y esté activo
        PacienteResponse pacienteResponse = obtenerPacienteActivo(request.idPaciente());

        //Valida qu el paciente no tenga citas activas
        consultaCitasActivasPorPaciente(pacienteResponse.id());

        Cita cita = citaMapper.requestAEntidad(request);

        citaRepository.save(cita);

        //Actualizar disponibilidad médico
        actualizarDisponibilidadMedico(request.idMedico(), CODIGO_NO_DISPONIBLE);

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

        if (EstadoCita.EN_CURSO.equals(cita.getEstadoCita()))
            throw new IllegalStateException("La cita no se puede actualizar porque se encuentra en estado " + cita.getEstadoCita());

        cita.validarActualizacionPermitida();

        PacienteResponse pacienteResponse = obtenerPacienteActivo(request.idPaciente());

        consultaCitasActivasPorPacienteActualizar(pacienteResponse.id(), cita.getId());

        MedicoResponse medicoResponse = obtenerMedicoActivo(request.idMedico());

        //Valida si es nuevo médico
        boolean esNuevoMedico = !cita.getIdMedico().equals(request.idMedico());

        if (esNuevoMedico) validarEstatusDisponibleMedico(medicoResponse.idDisponibilidad());

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas()
        );

        if (esNuevoMedico) {
            //Actualizar disponibilidad de médico "retirado"
            actualizarDisponibilidadMedico(medicoResponse.id(), CODIGO_NO_DISPONIBLE);

            //Actualizar disponibilidad de nuevo médico
            actualizarDisponibilidadMedico(cita.getIdMedico(), CODIGO_DISPONIBLE);
        }

        log.info("Cita actualizada con el id: {}", id);

        return citaMapper.entidadAResponse(
                cita,
                pacienteResponse,
                medicoResponse
        );
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {

        log.info("Actualizando estado de la cita con id: " + idCita );

        Cita cita = obtenerCitaOExcepcion(idCita);

        cita.validarActualizacionPermitida();

        MedicoResponse medicoResponse =
                obtenerMedicoSinEstado(cita.getIdMedico());

        existenCitasActivasMedicoOmitiendoCita(medicoResponse.id(), idCita);

        cita.actualizarEstadoCita(
                EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita)
        );

        citaRepository.save(cita);

        actualizarDisponibilidadMedico(medicoResponse.id(), obtenerNuevaDisponibilidadMedico(idEstadoCita));

        log.info("Estado de la cita {} actualizado correctamente", idCita);
    }

    @Override
    public void eliminar(Long id) {

        Cita cita = obtenerCitaOExcepcion(id);

        log.info("Eliminando cita con id: " + id);

        MedicoResponse medicoResponse = obtenerMedicoSinEstado(cita.getIdMedico());

        //cita.eliminar incluye la validación para impedir la eliminación con estados PENDIENTE, CANCELADA o FINALIZADA.
        cita.eliminar();

        // Solo actualiza disponibilidad de médico si la cita a eliminar se encuentra en estatus PENDIENTE
        if (EstadoCita.PENDIENTE.equals(cita.getEstadoCita()))
            actualizarDisponibilidadMedico(medicoResponse.id(), CODIGO_DISPONIBLE);

        log.info("Cita con id {} ha sido marcada como eliminada", id);
    }

    @Override
    public void consultarCitasActivasPorPaciente(Long idPaciente) {

        log.info("Listando todas las citas activas del paciente {}", idPaciente);

        PacienteResponse pacienteResponse = obtenerPacienteActivo(idPaciente);

        if (citaRepository.existsByIdPacienteAndEstadoCitaIn(idPaciente, ESTADOS_CITA))
            throw new IllegalStateException("El paciente tiene citas activas");
    }

    @Override
    public void consultarCitasActivasPorMedico(Long idMedico) {

        log.info("Listando todas las citas activas del médico {}", idMedico);

        MedicoResponse medicoResponse = obtenerMedicoActivo(idMedico);

        if (citaRepository.existsByIdMedicoAndEstadoCitaIn(idMedico, ESTADOS_CITA))
            throw new IllegalStateException("El médico tiene citas activas");
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

    private MedicoResponse actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        log.info("Actualizando disponibilidad de médico con id {} en el servicio remoto...", idDisponibilidad);

        return medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
    }

    private void validarEstatusDisponibleMedico(Long idDisponibilidad) {
        if (!DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad).getCodigo().equals(CODIGO_DISPONIBLE)) {
            throw new IllegalStateException("El médico no tiene un estatus disponible para el registro de la cita");
        }
    }

    private void consultaCitasActivasPorPaciente(Long idPaciente) {
        log.info("Validando citas activas del paciente con id {}", idPaciente);

        if (citaRepository.existsByIdPacienteAndEstadoCitaIn(idPaciente, ESTADOS_CITA_ACTIVOS))
            throw new IllegalStateException("El paciente tiene citas activas");
    }

    private void consultaCitasActivasPorPacienteActualizar(Long idPaciente, Long idCita) {
        log.info("Validando citas activas del paciente con id {}", idPaciente);

        if (citaRepository.existsByIdPacienteAndEstadoCitaInAndIdNot(idPaciente, ESTADOS_CITA_ACTIVOS, idCita))
            throw new IllegalStateException("El paciente tiene citas activas");
    }

    private void existenCitasActivasMedicoOmitiendoCita(Long idMedico, Long idCita) {
        log.info("Validando citas activas del médico con id {} omitiendo cita con id", idMedico, idCita);
        if (citaRepository.existsByIdMedicoAndEstadoCitaInAndIdNot(idMedico, ESTADOS_CITA, idCita))
            throw new IllegalStateException("El paciente tiene citas activas");
    }

    private Long obtenerNuevaDisponibilidadMedico(Long idEstadoCita) {
        EstadoCita estadoCita = EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);
        return switch (estadoCita) {
            case PENDIENTE, CONFIRMADA -> CODIGO_NO_DISPONIBLE;
            case EN_CURSO -> DisponibilidadMedico.EN_CONSULTA.getCodigo();
            case FINALIZADA, CANCELADA -> CODIGO_DISPONIBLE;
        };
    }
}
