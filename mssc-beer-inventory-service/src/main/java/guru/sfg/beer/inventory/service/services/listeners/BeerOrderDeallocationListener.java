package guru.sfg.beer.inventory.service.services.listeners;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.services.AllocationService;
import guru.sfg.brewery.model.events.DeallocateOrderRequest;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class BeerOrderDeallocationListener {
	
	private final AllocationService allocationService;
	
	@JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
	public void listen(DeallocateOrderRequest request) {
		allocationService.deallocateBeerOrder(request.getBeerOrderDto());
	}

}
