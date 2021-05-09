package ch.zhaw.rpa.dogguruwebhookhandler;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    @Bean
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(2);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("UiPathActions-");
    executor.initialize();
    return executor;
  }
}
