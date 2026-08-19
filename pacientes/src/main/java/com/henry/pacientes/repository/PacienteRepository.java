package com.henry.pacientes.repository;

import com.henry.pacientes.entity.Paciente;
import com.henry.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByIdAndEstadoRegistro(Long idPaciente, EstadoRegistro estadoRegistro);

    List<Paciente> findByEstadoRegistro(EstadoRegistro estadoRegistro);
}
