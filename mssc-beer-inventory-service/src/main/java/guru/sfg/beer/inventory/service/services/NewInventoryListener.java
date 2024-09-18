package guru.sfg.beer.inventory.service.services;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.events.NewInventoryEvent;
import guru.sfg.brewery.model.BeerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewInventoryListener {
	
	private final BeerInventoryRepository beerInventoryRepository;
	
	@Transactional
	@JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
	public void listen(NewInventoryEvent event) {
		log.debug("Got Inventory: " + event.toString());
		
		BeerDto beerDto = event.getBeerDto();
		
		BeerInventory beerInventory = BeerInventory.builder()
										.beerId(beerDto.getId())
										.upc(beerDto.getUpc())
										.quantityOnHand(beerDto.getQuantityOnHand())
										.build();
		
		beerInventoryRepository.save(beerInventory);
		
	}

}
