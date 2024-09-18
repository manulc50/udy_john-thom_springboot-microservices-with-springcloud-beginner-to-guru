package guru.sfg.beer.order.service.services.listeners;

import java.util.UUID;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import guru.sfg.brewery.model.events.ValidateBeerOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ValidationResultListener {
	
	private final BeerOrderManager beerOrderManager;
	
	@JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
	public void listen(ValidateBeerOrderResponse response) {
		UUID beerOrderId = response.getBeerOrderId();
		
		log.debug("Validation Result for Order Id: {}", beerOrderId);
		
		beerOrderManager.processValidationResult(beerOrderId, response.getIsValid());
	}

}
