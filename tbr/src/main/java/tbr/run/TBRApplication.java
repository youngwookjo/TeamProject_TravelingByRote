package tbr.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"tbr.controller", "tbr.service", "tbr.exception", "tbr.model.*"})
@EnableJpaRepositories(basePackages="tbr.model.dao")
@EntityScan("tbr.model.dto")
public class TBRApplication {

	public static void main(String[] args) {
		SpringApplication.run(TBRApplication.class, args);
	}

}