package guru.sfg.beer.inventory.service.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local-discovery") // Esta configuración sólo se va a tener en cuenta cuando esta aplicación Spring Boot se ejecute con el perfil "local-discovery"
@EnableDiscoveryClient // Esta anotación es para que el servidor Eureka descubra este microservicio y lo registre. En realidad, actualmente no hace falta añadir esta anotación porque, con incluir la dependencia adecuada("spring-cloud-starter-netflix-eureka-client") en el pom, el servidor Eureka lo descubre y lo registra automáticamente por defecto
@Configuration
public class LocalDiscovery {

}
