package de.yggdrasil128.factorial;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "de.yggdrasil128.factorial.model.*")
@EntityScan("de.yggdrasil128.factorial.model.*")
public class FactorialApplication {
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(FactorialApplication.class, args);
    }

    public static void restart(Runnable runWhileStopped) {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            if (runWhileStopped != null) {
                runWhileStopped.run();
            }
            context = SpringApplication.run(FactorialApplication.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

    public static void stop() {
        new Thread(context::close).run();
    }
}
