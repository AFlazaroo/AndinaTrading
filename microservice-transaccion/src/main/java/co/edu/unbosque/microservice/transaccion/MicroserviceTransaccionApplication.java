package co.edu.unbosque.microservice.transaccion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@enableEurekaClient
@SpringBootApplication
public class MicroserviceTransaccionApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceTransaccionApplication.class, args);
	}

}
