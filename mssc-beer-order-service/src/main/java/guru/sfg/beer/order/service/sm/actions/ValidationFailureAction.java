package guru.sfg.beer.order.service.sm.actions;

import java.util.UUID;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;

// Acción a realizar cuando la máquina de estados está en el estado "VALIDATION_PENDING" y se produce el evento "VALIDATION_FAILED"

@Slf4j
@Component
public class ValidationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

	@Override
	public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
		UUID beerOrderId = (UUID)context.getMessageHeader(BeerOrderManagerImpl.ORDER_ID_HEADER);
		
		log.error("Compensating Transaction... Validation failed: {}", beerOrderId);
		
	}

}
