package com.sfg.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery") // Esta configuración sólo se va a tener en cuenta cuando esta aplicación Spring Boot se ejecute con el perfil "local-discovery"
@Configuration
public class LoadBalancedRoutesConfig {
	
	/*
	  Nota:
	  * - Significa cualquier cosa que haya hasta el siguiente slash(/)
	  ** - Significa cualquier cosa que haya en ese nivel y en los siguientes subniveles, es decir, también se incluye lo que haya más allá de los siguientes slashes(/)
	  ? - Significa un sólo caracter que puede ser cualquier cosa. Por ejemplo, "/api/v1/te?t", coincide con "api/v1/test","api/v1/teat","api/v1/te8t", etc... 
	 */
	
	@Bean
	public RouteLocator localBalancedRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/api/v1/beer*","/api/v1/beer/*","/api/v1/beer/*/beerUpc") // Configuramos las rutas de este Gateway que coincidan con alguna de las expresiones "/api/v1/beer*","/api/v1/beer/*" o "/api/v1/beer/beerUpc/*"
						// Cuando la ruta de este Gateway sea alguna que coincida con alguna de las expresiones "/api/v1/beer*","/api/v1/beer/*" o "/api/v1/beer/beerUpc/*", queremos que se redirija a la url del microservicio con nombre "beer-service" concatenada con esa ruta(ya que esas rutas son las que se han configurado en los controladores REST de ese microservicio). La url del microservicio se obtiene del servidor Eureka a través de su nombre
						.uri("lb://beer-service") // "lb" delante la uri habilita el Balanceador de Carga(Por defecto, Ribbon) en este Gateway para las peticiones que se hagan a este microservicio si existiese más de una instancia
						.id("beer-service")) // Identificador de la ruta
				.route(r -> r.path("/api/v1/customers/**") // Configuramos las rutas de este Gateway que coincidan con la expresión "/api/v1/customers/**"
						// Cuando la ruta de este Gateway sea alguna que coincida con la expresión "/api/v1/customers/**", queremos que se redirija a la url del microservicio con nombre "beer-order-service" concatenada con esa ruta(ya que esas rutas son las que se han configurado en los controladores REST de ese microservicio). La url del microservicio se obtiene del servidor Eureka a través de su nombre
						.uri("lb://beer-order-service")  // "lb" delante la uri habilita el Balanceador de Carga(Por defecto, Ribbon) en este Gateway para las peticiones que se hagan a este microservicio si existiese más de una instancia
						.id("beer-order-service")) // Identificador de la ruta
				.route(r -> r.path("/api/v1/beer/*/inventory") // Configuramos las rutas de este Gateway que coincidan con la expresión "/api/v1/beer/*/inventory"
						.filters(f -> f.circuitBreaker(c -> c.setName("inventoryCB") // Configuramos un filtro para crear un Circuit Breaker(Implementado con Resilience4j) llamado "inventoryCB"
										.setFallbackUri("forward:/inventory-failover") // Este Circuit Breaker redirige a la ruta "/inventory-failover" de este Gateway en caso de que el microservicio con nombre "beer-inventory-service" no responda o esté caído. De esta manera, como camino alternativo a este fallo, se invocará al microservicio con nombre "inventory-failover" para que dé una respuesta
										.setRouteId("inv-failover") // Identificador de la ruta de este Circuit Breaker
										))
						// Cuando la ruta de este Gateway sea alguna que coincida con la expresión "/api/v1/beer/*/inventory", queremos que se redirija a la url del microservicio con nombre "beer-inventory-service" concatenada con esa ruta(ya que esas rutas son las que se han configurado en los controladores REST de ese microservicio). La url del microservicio se obtiene del servidor Eureka a través de su nombre
						.uri("lb://beer-inventory-service")  // "lb" delante la uri habilita el Balanceador de Carga(Por defecto, Ribbon) en este Gateway para las peticiones que se hagan a este microservicio si existiese más de una instancia
						.id("beer-inventory-service")) // Identificador de la ruta
				.route(r -> r.path("/inventory-failover") // Configuramos la ruta "/inventory-failover" de este Gateway
						// Cuando la ruta de este Gateway sea "/inventory-failover", queremos que se redirija a la url del microservicio con nombre "inventory-failover" concatenada con esa ruta(ya que esa ruta es la que se ha configurado en el controlador REST de ese microservicio). La url del microservicio se obtiene del servidor Eureka a través de su nombre
						.uri("lb://inventory-failover") // "lb" delante la uri habilita el Balanceador de Carga(Por defecto, Ribbon) en este Gateway para las peticiones que se hagan a este microservicio si existiese más de una instancia
						.id("inventory-failover-service")) // Identificador de la ruta
				.build();
	}


}
