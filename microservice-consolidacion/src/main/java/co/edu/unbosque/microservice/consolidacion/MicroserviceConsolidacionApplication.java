package co.edu.unbosque.microservice.consolidacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@enableEurekaClient
@SpringBootApplication
public class MicroserviceConsolidacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceConsolidacionApplication.class, args);
	}

}
