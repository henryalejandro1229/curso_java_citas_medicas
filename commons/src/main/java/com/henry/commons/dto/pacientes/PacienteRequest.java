package com.henry.commons.dto.pacientes;

import jakarta.validation.constraints.*;

public record PacienteRequest(

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

        @NotNull(message = "El peso es un campo requerido")
        @DecimalMin(value = "0.1", message = "El peso mínimo requerido es 0.1 kg")
        @DecimalMax(value = "200.0", message = "El peso máximo requerido es 200 kg")
        Double peso,

        @NotNull(message = "La estatura es un campo requerido")
        @DecimalMin(value = "1.0", message = "La estatura mínima requerida es 1 m")
        @DecimalMax(value = "2.0", message = "La estatura máxima requerida es 2 m")
        Double estatura,

        @NotBlank(message = "El email es un campo requerido")
        @Size(min = 5, max = 100, message = "El email debe tener entre 5 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @NotBlank(message = "El teléfono es un campo requerido")
        @Pattern(regexp = "^[0-9]{10}", message = "El teléfono debe contener exactamente 10 dígitos")
        String telefono,

        @NotBlank(message = "La dirección es un campo requerido")
        @Size(min = 1, max = 150, message = "La dirección debe tener entre 1 y 150 caracteres")
        String direccion
        ) {
}
