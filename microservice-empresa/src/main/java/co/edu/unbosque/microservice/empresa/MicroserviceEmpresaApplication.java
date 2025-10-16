package co.edu.unbosque.microservice.empresa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient; // <-- CONFIRMA ESTA LÍNEA

@EnableEurekaClient // <-- Y CONFIRMA ESTA LÍNEA
@SpringBootApplication
public class MicroserviceEmpresaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceEmpresaApplication.class, args);
    }
}