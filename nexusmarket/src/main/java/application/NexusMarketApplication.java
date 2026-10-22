package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NexusMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexusMarketApplication.class, args);
    }
}
