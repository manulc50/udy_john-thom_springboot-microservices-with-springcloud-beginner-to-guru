package com.sfg.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!local-discovery") // Esta configuración sólo se va a tener en cuenta cuando esta aplicación Spring Boot no se ejecute con el perfil "local-discovery"
@Configuration
public class LocalHostRouteConfig {
	
	/*
	  Nota:
	  * - Significa cualquier cosa que haya hasta el siguiente slash(/)
	  ** - Significa cualquier cosa que haya en ese nivel y en los siguientes subniveles, es decir, también se incluye lo que haya más allá de los siguientes slashes(/)
	  ? - Significa un sólo caracter que puede ser cualquier cosa. Por ejemplo, "/api/v1/te?t", coincide con "api/v1/test","api/v1/teat","api/v1/te8t", etc... 
	 */
	
	@Bean
	public RouteLocator localHostRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/api/v1/beer*","/api/v1/beer/*","/api/v1/beer/*/beerUpc") // Configuramos las rutas de este Gateway que coincidan con alguna de las expresiones "/api/v1/beer*","/api/v1/beer/*" o "/api/v1/beer/beerUpc/*"
						.uri("http://localhost:8080") // Cuando la ruta de este Gateway sea alguna que coincida con alguna de las expresiones "/api/v1/beer*","/api/v1/beer/*" o "/api/v1/beer/beerUpc/*", queremos que se redirija a la url del microservicio "beer-service", que  se encuentra en la url "http://localhost:8080", concatenada con esa ruta(ya que esas rutas son las que se han configurado en los controladores REST de ese microservicio)
						.id("beer-service")) // Identificador de la ruta
				.route(r -> r.path("/api/v1/customers/**") // Configuramos las rutas de este Gateway que coincidan con la expresión "/api/v1/customers/**"
						.uri("http://localhost:8081") // Cuando la ruta de este Gateway sea alguna que coincida con la expresión "/api/v1/customers/**", queremos que se redirija a la url del microservicio "beer-order-service", que  se encuentra en la url "http://localhost:8081", concatenada con esa ruta(ya que esas rutas son las que se han configurado en los controladores REST de ese microservicio)
						.id("beer-order-service")) // Identificador de la ruta
				.route(r -> r.path("/api/v1/beer/*/inventory") // Configuramos las rutas de este Gateway que coincidan con la expresión "/api/v1/beer/*/inventory"
						.uri("http://localhost:8082") // Cuando la ruta de este Gateway sea alguna que coincida con la expresión "/api/v1/beer/*/inventory", queremos que se redirija a la url del microservicio "beer-inventory-service", que  se encuentra en la url "http://localhost:8082", concatenada con esa ruta(ya que esas rutas son las que se han configurado en los controladores REST de ese microservicio)
						.id("beer-inventory-service")) // Identificador de la ruta
				.build();
	}

}
