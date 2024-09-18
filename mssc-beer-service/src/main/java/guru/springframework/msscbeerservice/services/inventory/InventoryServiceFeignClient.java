package guru.springframework.msscbeerservice.services.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import guru.sfg.brewery.model.BeerInventoryDto;
import guru.springframework.msscbeerservice.config.FeignClientConfig;

// El cliente Feign de Netflix utiliza por defecto el Balanceador de Carga Ribbon(El Balanceador de Carga se aplica si hubiese o existiese más de una instancia del microservicio "beer-inventory-service") 

// El cliente Feign va a comunicarse con el microservicio "beer-inventory-service"
// Si el microservicio "beer-inventory-service" no response o está caído, se ejecuta, como protocolo Circuit Breaker(actualemente usando Hystrix), el camino alternativo a este fallo cuya implementación se encuentra en la clase "InventoryServiceFeignClientFailover"
// En el atributo "configuration" indicamos que el cliente Feign de Netflix utilice nuestra configuración personalizada implementada en la clase de configuración "FeignClientConfig" que proporciona autenticación básica(Http Basic Authentication) para poder indicar las credenciales requeridas por el microservicio "mssc-beer-inventory-service", que es el microservicio con el que vamos a comunicarnos
@FeignClient(name = "beer-inventory-service", fallback = InventoryServiceFeignClientFailover.class, configuration = FeignClientConfig.class)
public interface InventoryServiceFeignClient {
	
	// Realiza una petición http Get a la ruta o path indicada en "INVENTORY_PATH" del microservicio "beer-inventory-service"
	// Este método tiene que devolver y recibir los mismos tipos de datos que se devuelve y se recibe en el método handler correspondiente del microservicio "beer-inventory-service" asociado a la ruta indicada en "INVENTORY_PATH" para peticiones http Get 
	@RequestMapping(method = RequestMethod.GET, value = BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
	ResponseEntity<List<BeerInventoryDto>> listBeersById(@PathVariable UUID beerId);

}
