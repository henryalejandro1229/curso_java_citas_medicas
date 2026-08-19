package com.henry.pacientes.entity;

import com.henry.commons.enums.EstadoRegistro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PACIENTES")
@Getter
@Builder
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numExpediente;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    public void generarNumExpediente() {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < telefono.length(); i++) {
            resultado.append(telefono.charAt(i));

            if (i < telefono.length() - 1) {
                resultado.append("X");
            }
        }

        numExpediente = resultado.toString();
    }

    public void calcularIMC() {
        imc = peso / Math.pow(estatura, 2);
    }

    public void actualizar(String nombre, String apellidoPaterno,
                           String apellidoMaterno, Short edad, Double peso,
                           Double estatura, String email, String telefono,
                           String direccion) {
        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.edad = edad;
        this.peso = peso;
        this.estatura = estatura;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;

        calcularIMC();

        generarNumExpediente();
    }

    public void setEstatusEliminado() {
        estadoRegistro = EstadoRegistro.ELIMINADO;
    }
}
