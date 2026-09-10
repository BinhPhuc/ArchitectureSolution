package com.architecture.solution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ArchitectureSolutionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchitectureSolutionApplication.class, args);
    }

}
