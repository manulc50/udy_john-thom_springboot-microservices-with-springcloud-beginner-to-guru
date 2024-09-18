package guru.sfg.beer.order.service.sm.actions;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import guru.sfg.brewery.model.events.DeallocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// Acción a realizar cuando la máquina de estados está en el estado "ALLOCATED" y se produce el evento "CANCEL_ORDER"

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

	private final JmsTemplate jmsTemplate;
	private final BeerOrderRepository beerOrderRepository;
	private final BeerOrderMapper beerOrderMapper;
	
	
	@Override
	public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
		UUID beerOrderId = (UUID)context.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
		Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderId);
		
		beerOrderOptional.ifPresentOrElse(beerOrder -> {
			jmsTemplate.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE,
					DeallocateOrderRequest.builder()
						.beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
						.build());
		},() -> log.error("Order Not Found. Id: " + beerOrderId));
		
		log.debug("Sent Deallocation request to queue for order id: {}", beerOrderId);
		
	}
	

}
