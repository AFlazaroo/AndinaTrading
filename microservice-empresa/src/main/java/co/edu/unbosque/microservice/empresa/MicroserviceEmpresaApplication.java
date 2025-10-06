package co.edu.unbosque.microservice.empresa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@enableEurekaClient
@SpringBootApplication
public class MicroserviceEmpresaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceEmpresaApplication.class, args);
	}

}
