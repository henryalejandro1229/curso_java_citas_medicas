package com.henry.citas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.henry.commons.dto.medicos.DatosMedico;
import com.henry.commons.dto.pacientes.DatosPaciente;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        DatosPaciente paciente,
        DatosMedico medico,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyy HH:mm")
        LocalDateTime fechaCita,
        String sintomas,
        String estadoCita
) {
}
