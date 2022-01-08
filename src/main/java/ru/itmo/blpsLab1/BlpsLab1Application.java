package ru.itmo.blpsLab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.itmo.blpsLab1.config.AdminCredentialsProperties;

@SpringBootApplication
@EnableConfigurationProperties({AdminCredentialsProperties.class})
public class BlpsLab1Application {
	public static void main(String[] args) {
		SpringApplication.run(BlpsLab1Application.class, args);
	}
}
