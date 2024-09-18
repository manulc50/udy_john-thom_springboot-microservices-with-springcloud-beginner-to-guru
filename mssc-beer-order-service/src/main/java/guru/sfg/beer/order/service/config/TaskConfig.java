package guru.sfg.beer.order.service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Task Configuration - enable asyc tasks
 */
@EnableScheduling
@EnableAsync
@Configuration
// Usamos esta propiedad para poder desactivar la tarea programada de la clase "TastinRoomService" cuando ejecutemos las pruebas de integración de la clase "BeerOrderManagerImplIT"
@ConditionalOnProperty(
        name = "guru.sfg.beer.order.service.scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class TaskConfig {
	
    @Bean
    TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
