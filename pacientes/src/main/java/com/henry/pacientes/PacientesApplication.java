package com.henry.pacientes;

import com.henry.commons.exceptions.GlobalHandlerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = {"com.henry.pacientes", "com.henry.commons"})
@EnableFeignClients
@Import(GlobalHandlerException.class)
public class PacientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesApplication.class, args);
	}

}
