package tw.amer.cia.core;

import tw.amer.cia.core.component.structural.info.MethodCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableConfigurationProperties
@SpringBootApplication
@EnableScheduling
public class CorianderIngressAPI {
    @Autowired
    private MethodCounter methodCounter;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CorianderIngressAPI.class);
        app.run(args);
    }
    @Bean
    public CommandLineRunner schedulingRunner() {
        return args -> {
            methodCounter.startLogging(1, TimeUnit.MINUTES);
        };
    }
}
