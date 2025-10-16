package co.edu.unbosque.microservice.ibkradapter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceIbkrAdapterApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceIbkrAdapterApplication.class, args);
    }
}