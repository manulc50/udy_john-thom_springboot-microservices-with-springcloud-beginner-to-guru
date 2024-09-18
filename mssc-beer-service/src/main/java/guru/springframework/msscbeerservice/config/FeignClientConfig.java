package guru.springframework.msscbeerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.auth.BasicAuthRequestInterceptor;

// Esta clase de configuración de Spring es necesaria para configurar el cliente Feign de Netflix para que soporte autenticación básica(Http Basic Authentication) y así poder indicar las credenciales requeridas por el microservicio "mssc-beer-inventory-service", que es el microservicio con el que vamos a comunicarnos

@Configuration
public class FeignClientConfig {
	
	@Bean
	public BasicAuthRequestInterceptor basicAuthRequestInterceptor(@Value("${sfg.brewery.inventory-user}") String inventoryUser,
																   @Value("${sfg.brewery.inventory-password}") String inventoryPassword) {
		
		return new BasicAuthRequestInterceptor(inventoryUser,inventoryPassword);
	}

}
