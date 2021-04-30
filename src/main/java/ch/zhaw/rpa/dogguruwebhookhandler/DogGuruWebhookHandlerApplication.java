package ch.zhaw.rpa.dogguruwebhookhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hauptklasse für die RestService-Template-SpringBoot-Applikation
 * 
 * @author scep
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DogGuruWebhookHandlerApplication {
    public static void main(String[] args){
        SpringApplication.run(DogGuruWebhookHandlerApplication.class, args);
    }
}
