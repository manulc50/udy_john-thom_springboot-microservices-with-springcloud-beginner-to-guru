package guru.sfg.beer.order.service.sm.interceptors;

import java.util.Optional;
import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// Interceptor de la máquina de estados que actualiza el estado de los pedidos de cervezas en la base de datos antes de que se produzca un cambio de estado

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

	private final BeerOrderRepository beerOrderRepository;

	@Override
	public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state,
			Message<BeerOrderEventEnum> message, Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
			StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine,
			StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> rootStateMachine) {
		
		log.debug("Pre-State Change");
		
		Optional.ofNullable(message)
			.ifPresent(msg -> Optional.ofNullable((UUID)msg.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, ""))
				.ifPresent(beerOrderId -> {
					log.debug("Saving state for order id: {}, status: {}", beerOrderId, state.getId());
					
					BeerOrder beerOrder = beerOrderRepository.findById(beerOrderId).get();
					beerOrder.setOrderStatus(state.getId());
					beerOrderRepository.saveAndFlush(beerOrder);
				}));
	}
		

	
	
}
