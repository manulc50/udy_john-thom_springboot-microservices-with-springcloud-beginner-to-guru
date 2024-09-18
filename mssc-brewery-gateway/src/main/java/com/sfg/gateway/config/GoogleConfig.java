package com.sfg.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("google") // Esta configuración sólo se va a tener en cuenta cuando el perfil seleccionado para ejecutar esta aplicación Spring Boot sea "google"
@Configuration
public class GoogleConfig {
	
	@Bean
	public RouteLocator googleRouteConfig(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/googlesearch") // Configuramos la ruta o path "/googlesearch"
						// Por defecto, la ruta configurada arriba se añade o se concatena a la uri o url de la redirección("https://google.com/googlesearch")
						// Entonces, como no queremos ir a esa uri o url, configuramos un filtro para eliminar, antes de producirse la redirección, la parte "/googlesearch" de la ruta o path que no queremos que se añada a la url o uri donde se va a realizar esa redirección
						.filters(f -> f.stripPrefix(1)) // El método "stripPrefix" recibe el número de partes de la ruta o path configurada que queremos eliminar de la uri o url.Entonces, como sólo tenemos una parte en la ruta o path("/googlesearch"),la eliminamos.Si fuese "/googlesearch/hola", el número de partes sería 2. Si fuese "/googlesearch/hola", y lo dejamos con 1, la uri o url final sería "https://google.com/hola"
						.uri("https://google.com") // Cuando la ruta de este Gateway sea "/googlesearch", queremos que se redirija a la url "https://google.com"
						.id("google")) // Identificador de la ruta
				.build();
	}

}
