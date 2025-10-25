package eu.invouk.nexuschunk;

import eu.invouk.nexuschunk.auth.model.RecaptchaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RecaptchaProperties.class)
public class NexuschunkApplication {

	static void main(String[] args) {
		SpringApplication.run(NexuschunkApplication.class, args);
	}

}
