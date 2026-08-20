package com.henry.commons.client;

import com.henry.commons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "medicos")
public interface MedicoClient {

    @GetMapping("/{id}")
    MedicoResponse obtenerMedicoActivoPorId(@PathVariable Long id);

    @GetMapping("/id-medico/{id}")
    MedicoResponse obtenerMedicoPorIdSinEstado(@PathVariable Long id);

    @PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
    MedicoResponse actualizarDisponibilidadMedico(
            @PathVariable Long idMedico,
            @PathVariable Long idDisponibilidad);
}
