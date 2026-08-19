package com.henry.commons.dto.medicos;

import jakarta.validation.constraints.*;

public record MedicoRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @NotNull(message = "La edad es un campo requerido")
        @Min(value = 1, message = "La edad mímina requerida es 1 años")
        @Max(value = 100, message = "La edad máxima requerida es 100 años")
        Short edad,

        @NotBlank(message = "El email es un campo requerido")
        @Size(min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @NotBlank(message = "El teléfono es un campo requerido")
        @Pattern(regexp = "^[0-9]{10}", message = "El teléfono debe contener exactamente 10 dígitos")
        String telefono,

        @NotBlank(message = "La cédula profesional es requerida")
        @Size(min = 12, max = 12, message = "La cédula profesional debe tener exactamente 12 caracteres")
        String cedulaProfesional,

        @NotNull(message = "El id de la especialidad es requerio")
        @Positive(message = "El id de la especialidad debe ser positivo")
        Long idEspecialidad
) {
}
