package com.production;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages= {"com.organisation.entity","com.organisation.customers","com.operational","com.organisation.controllers","com.organisation.services"})
@EntityScan(basePackages={"com.organisation.entity"})
@EnableJpaRepositories(basePackages={"com.organisation.repositories"})
public class CapstonePjtApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapstonePjtApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
	 
	  return builder
	    .setConnectTimeout(Duration.ofMillis(3000))
	    .setReadTimeout(Duration.ofMillis(3000))
	    .build();
	}

}
