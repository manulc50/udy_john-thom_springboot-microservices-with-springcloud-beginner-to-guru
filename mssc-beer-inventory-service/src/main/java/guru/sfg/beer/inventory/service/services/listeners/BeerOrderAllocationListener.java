package guru.sfg.beer.inventory.service.services.listeners;


import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.services.AllocationService;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class BeerOrderAllocationListener {
	
	private final AllocationService allocationService;
	private final JmsTemplate jmsTemplate;
	
	@JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
	public void listen(AllocateOrderRequest request) {
		BeerOrderDto beerOrderDto= request.getBeerOrderDto();
		
		AllocateOrderResponse.AllocateOrderResponseBuilder builder = AllocateOrderResponse.builder();
		builder.beerOrderDto(beerOrderDto);
		builder.allocationError(false);
		builder.pendingInventory(false);
		
		try {
			builder.pendingInventory(allocationService.allocateOrder(beerOrderDto));
		}
		catch(Exception e) {
			log.error("Allocation failed for Order Id: {}", beerOrderDto.getId());
			builder.allocationError(true);
		}
		
		jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, builder.build());

	}

}
