package guru.springframework.msscbeerservice.services.inventory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.model.BeerInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Profile("local-discovery") // Esta configuración sólo se va a tener en cuenta cuando esta aplicación Spring Boot se ejecute con el perfil "local-discovery".De esta manera, nos aseguramos de que sólo haya una implementación de la interfaz "BeerInventoryService" cada vez que se ejecute la aplicación 
@RequiredArgsConstructor
@Service
public class BeerInventoryServiceFeignImpl implements BeerInventoryService{
	
	private final InventoryServiceFeignClient inventoryServiceFeignClient;

	@Override
	public Integer getOnHandInventory(UUID beerId) {
		log.debug("Calling Inventory Service - BeerId: " + beerId);
		
		ResponseEntity<List<BeerInventoryDto>> responseEntity = inventoryServiceFeignClient.listBeersById(beerId);
		
		Integer onHand = Objects.requireNonNull(responseEntity.getBody())
				.stream()
				.mapToInt(beerInventoryDto -> beerInventoryDto.getQuantityOnHand())
				.sum();
		
		log.debug("BeerId: " + beerId + " On hand is: " + onHand);

		return onHand;
	}

}
