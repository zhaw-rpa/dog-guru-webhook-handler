package ch.zhaw.rpa.dogguruwebhookhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Hauptklasse für die RestService-Template-SpringBoot-Applikation
 * 
 * @author scep
 */
@SpringBootApplication
@EnableAsync
public class DogGuruWebhookHandlerApplication {
    public static void main(String[] args){
        SpringApplication.run(DogGuruWebhookHandlerApplication.class, args);
    }
}
