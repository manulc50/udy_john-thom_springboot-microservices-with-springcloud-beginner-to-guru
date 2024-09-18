package guru.springframework.msscbeerservice.services.brewing;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.brewery.model.events.BrewBeerEvent;
import guru.springframework.msscbeerservice.config.JmsConfig;
import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BrewingService {
	
	@Autowired
	private BeerRepository beerRepository;
	
	@Autowired
	private BeerInventoryService beerInventoryService;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private BeerMapper beerMapper;
	
	@Transactional(readOnly = true)
	//@Async
	@Scheduled(fixedRate = 5000) // Every 5 seconds
	public void checkForLowInventory() {
		List<Beer> beers = beerRepository.findAll();
		beers.forEach(beer -> {
			Integer invQOH = beerInventoryService.getOnHandInventory(beer.getId());
			
			log.debug("Min Onhand is: " + beer.getMinOnHand());
			log.debug("Inventory is: " + invQOH);
			
			if(beer.getMinOnHand() >= invQOH)
				jmsTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE,new BrewBeerEvent(beerMapper.beerToBeerDto(beer)));
		});
	}

}
