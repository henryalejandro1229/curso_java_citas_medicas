package com.henry.commons.client;

import com.henry.commons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas")
public interface CitaClient {

    @GetMapping("/id-paciente/{id}")
    void existenCitasActivasPorPaciente(@PathVariable Long id);

    @GetMapping("/id-medico/{id}")
    void existenCitasActivasPorMedico(@PathVariable Long id);
}
