package org.vogel.kubernetes.logreader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LogReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogReaderApplication.class, args);
    }

}
