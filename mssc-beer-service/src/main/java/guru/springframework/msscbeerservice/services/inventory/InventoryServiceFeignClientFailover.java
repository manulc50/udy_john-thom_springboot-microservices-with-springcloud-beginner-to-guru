package guru.springframework.msscbeerservice.services.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import guru.sfg.brewery.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;

/* Esta clase implementa el camino alternativo cuando el microservicio "beer-inventory-service"  no responde o está caído.
   Para ello, esta clase crea una nueva implementación de la interfaz "InventoryServiceFeignClient", que representa el cliente Feign que se comunica con ese microservicio,
   para obtener del microsevicio "inventory-failover" la cantidad en mano por defecto(999) de cada cerveza existente en el sistema en lugar de obtener las cantidades del microservicio "beer-inventory-service"
   La implementación de la interfaz "InventoryServiceFeignClient" se inyecta y se utiliza en la clase Servicio "BeerInventoryServiceFeignImpl"
 */

@Profile("local-discovery") // Esta configuración sólo se va a tener en cuenta cuando esta aplicación Spring Boot se ejecute con el perfil "local-discovery"
@Component
@RequiredArgsConstructor
public class InventoryServiceFeignClientFailover implements InventoryServiceFeignClient{
	
	private final InventoryFailoverFeignClient inventoryFailoverFeignClient;

	@Override
	public ResponseEntity<List<BeerInventoryDto>> listBeersById(UUID beerId) {
		return inventoryFailoverFeignClient.listBeers();
	}

}
