package com.henry.pacientes.mapper;

import com.henry.commons.dto.pacientes.PacienteRequest;
import com.henry.commons.dto.pacientes.PacienteResponse;
import com.henry.pacientes.entity.Paciente;
import com.henry.commons.enums.EstadoRegistro;
import org.springframework.stereotype.Component;

@Component
public class PacienteMapper {
    public Paciente requestAEntidad(PacienteRequest request) {
        if (request == null) return null;

        return Paciente.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .edad(request.edad())
                .peso(request.peso())
                .estatura(request.estatura())
                .email(request.email())
                .telefono(request.telefono())
                .direccion(request.direccion())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    public PacienteResponse entidadAResponse(Paciente entidad) {
        if (entidad == null) return null;

        return new PacienteResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEdad(),
                entidad.getPeso(),
                entidad.getEstatura(),
                entidad.getImc(),
                entidad.getEmail(),
                entidad.getNumExpediente(),
                entidad.getTelefono(),
                entidad.getDireccion()
        );
    }
}
