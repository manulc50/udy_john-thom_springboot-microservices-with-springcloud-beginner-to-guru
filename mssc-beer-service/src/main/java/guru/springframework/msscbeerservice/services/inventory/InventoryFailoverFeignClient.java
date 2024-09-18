package guru.springframework.msscbeerservice.services.inventory;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import guru.sfg.brewery.model.BeerInventoryDto;

// El cliente Feign de Netflix utiliza por defecto el Balanceador de Carga Ribbon(El Balanceador de Carga se aplica si hubiese o existiese más de una instancia del microservicio "inventory-failover") 

// El cliente Feign va a comunicarse con el microservicio "inventory-failover"
@FeignClient(name = "inventory-failover")
public interface InventoryFailoverFeignClient {
	
	// Realiza una petición http Get a la ruta o path "/inventory-failover" del microservicio "inventory-failover"
	// Este método tiene que devolver y recibir los mismos tipos de datos que se devuelve y se recibe en el método handler correspondiente del microservicio "inventory-failover" asociado a la ruta "/inventory-failover" para peticiones http Get 
	@RequestMapping(method = RequestMethod.GET, value = "/inventory-failover")
	ResponseEntity<List<BeerInventoryDto>> listBeers();


}
