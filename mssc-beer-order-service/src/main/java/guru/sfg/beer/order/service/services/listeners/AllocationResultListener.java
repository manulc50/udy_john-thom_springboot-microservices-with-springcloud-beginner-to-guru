package guru.sfg.beer.order.service.services.listeners;


import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.events.AllocateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationResultListener {

	private final BeerOrderManager beerOrderManager;
	
	@JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
	public void listen(AllocateOrderResponse response) {
		BeerOrderDto beerOrderDto = response.getBeerOrderDto();
		
		log.debug("Allocation Result for Order Id: {}", beerOrderDto.getId());
		
		beerOrderManager.processAllocationResult(beerOrderDto, response.getAllocationError(), response.getPendingInventory());
	}
}
