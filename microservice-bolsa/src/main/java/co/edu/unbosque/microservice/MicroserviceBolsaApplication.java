package co.edu.unbosque.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@enableEurekaClient
@SpringBootApplication
public class MicroserviceBolsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceBolsaApplication.class, args);
	}

}
