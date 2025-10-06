package co.edu.unbosque.microservice.portafolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@enableEurekaClient
@SpringBootApplication
public class MicroservicePortafolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicePortafolioApplication.class, args);
	}

}
