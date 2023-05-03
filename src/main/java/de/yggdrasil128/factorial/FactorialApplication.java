package de.yggdrasil128.factorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "de.yggdrasil128.factorial.repository")
@EntityScan("de.yggdrasil128.factorial.model")
public class FactorialApplication {
    public static void main(String[] args) {
        SpringApplication.run(FactorialApplication.class, args);
    }
}