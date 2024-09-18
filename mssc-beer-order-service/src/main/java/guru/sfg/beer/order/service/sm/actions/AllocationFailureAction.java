package guru.sfg.beer.order.service.sm.actions;

import java.util.UUID;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.brewery.model.events.AllocateFailureCompensationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// Acción a realizar cuando la máquina de estados está en el estado "ALLOCATION_PENDING" y se produce el evento "ALLOCATION_FAILED"

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {
	
	private final JmsTemplate jmsTemplate;
	
	@Override
	public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
		UUID beerOrderId = (UUID)context.getMessageHeader(BeerOrderManagerImpl.ORDER_ID_HEADER);
		
		jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_FAILURE_QUEUE,
				AllocateFailureCompensationRequest.builder()
					.beerOrderId(beerOrderId)
					.build());

		log.debug("Sent Allocation Failure Message to queue for order id {}", beerOrderId);
		log.error("Compensating Transaction... Allocation failed: {}", beerOrderId);
		
	}

}
