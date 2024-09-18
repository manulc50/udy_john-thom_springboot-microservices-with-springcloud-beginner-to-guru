package guru.sfg.brewery.inventoryfailover.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import guru.sfg.brewery.inventoryfailover.web.controller.InventoryHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterConfig {
	
	@Autowired
	@Bean
	public RouterFunction<ServerResponse> inventoryRoute(InventoryHandler inventoryHandler) {
		return route(GET("/inventory-failover").and(accept(MediaType.APPLICATION_JSON)),inventoryHandler::listBeers);
	}

}
