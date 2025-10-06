package co.edu.unbosque.microservice.reporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@enableEurekaClient
@SpringBootApplication
public class MicroserviceReporteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceReporteApplication.class, args);
	}

}
