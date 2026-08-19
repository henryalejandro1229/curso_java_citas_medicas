package com.henry.citas.controller;

import com.henry.citas.dto.CitaRequest;
import com.henry.citas.dto.CitaResponse;
import com.henry.citas.service.CitaService;
import com.henry.commons.controller.CommonController;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CitaController extends CommonController<CitaRequest, CitaResponse, CitaService> {

    public CitaController(CitaService service) {
        super(service);
    }

    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado
    ) {
        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/id-paciente/{id}")
    public ResponseEntity<Void> citasActivaPorPaciente(@PathVariable Long id) {

        service.consultarCitasActivasPorPaciente(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/id-medico/{id}")
    public ResponseEntity<Void> citasActivaPorMedico(@PathVariable Long id) {

        service.consultarCitasActivasPorMedico(id);

        return ResponseEntity.noContent().build();
    }
}
