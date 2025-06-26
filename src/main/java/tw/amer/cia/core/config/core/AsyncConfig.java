package tw.amer.cia.core.config.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Necessary for SpringBoot to scan @Async Functions
}
