package guru.springframework.msscbeerservice.services.order;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import guru.sfg.brewery.model.events.ValidateBeerOrderRequest;
import guru.sfg.brewery.model.events.ValidateBeerOrderResponse;
import guru.springframework.msscbeerservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BeerOrderValidationListener {

	private final BeerOrderValidator validator;
	private final JmsTemplate jmsTemplate;
	
	@JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
	public void listen(ValidateBeerOrderRequest request) {
		Boolean isValid = validator.validateOrder(request.getBeerOrderDto());
		
		jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, ValidateBeerOrderResponse.builder()
				.beerOrderId(request.getBeerOrderDto().getId())
				.isValid(isValid)
				.build());
	}

}
