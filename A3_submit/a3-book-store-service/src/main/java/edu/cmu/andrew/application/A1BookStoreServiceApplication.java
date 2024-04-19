package edu.cmu.andrew.application;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
* <h1>Main Springboot applciation class</h1>
* 
* Execution point for Springboot applciation
*  
* @author  Juan Carlos Villegas Montiel
* @version 1.0
* @since   2024-03-19
*/
@SpringBootApplication
@EnableFeignClients
public class A1BookStoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(A1BookStoreServiceApplication.class, args);
	}
	
	  @Bean
	    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
	    	return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
	    			.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3)).build())
	    			.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
	    			.build());
	    }

}
