package guru.sfg.beer.order.service.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.sm.interceptors.BeerOrderStateChangeInterceptor;
import guru.sfg.brewery.model.BeerOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class BeerOrderManagerImpl implements BeerOrderManager {
	
	public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";

	private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
	private final BeerOrderRepository beerOrderRepository;
	private final BeerOrderStateChangeInterceptor beerOrderStateChangeInterceptor;
	
	@Transactional
	@Override
	public BeerOrder newBeerOrder(BeerOrder beerOrder) {
		beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
		
		BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
		
		sendBeerOrderEvent(savedBeerOrder, BeerOrderEventEnum.VALIDATE_ORDER);
		
		return savedBeerOrder;
	}
	
	@Transactional
	@Override
	public void processValidationResult(UUID beerOrderId, Boolean isValid) {
		log.debug("Process Validation Result for beerOrderId: " + beerOrderId + " Valid? " + isValid);
		
		Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderId);

		beerOrderOptional.ifPresentOrElse(beerOrder -> {
			if(isValid) {
				sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_PASSED);		
				
				sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATE_ORDER);
			}
			else
				sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.VALIDATION_FAILED);
		}, () -> log.error("Order Not Found. Id: " + beerOrderId));
	}
	
	@Transactional
	@Override
	public void processAllocationResult(BeerOrderDto beerOrderDto, Boolean allocationError, Boolean pendingInventory) {
		Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderDto.getId());
		
		beerOrderOptional.ifPresentOrElse(beerOrder -> {
			if(allocationError)
				sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_FAILED);
			else {
				if(pendingInventory)
					sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_NO_INVENTORY);
				else
					sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.ALLOCATION_SUCCESS);
				
				updateAllocateQty(beerOrderDto, beerOrder);
			}
		}, () -> log.error("Order Not Found. Id: " + beerOrderDto.getId()));
	}
	
	@Override
	public void pickedUpBeerOrder(UUID beerOrderId) {
		Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderId);
		
		beerOrderOptional.ifPresentOrElse(beerOrder -> {
			sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.BEERORDER_PICKED_UP);
		}, () -> log.error("Order Not Found. Id: " + beerOrderId));
	}
	
	@Override
	public void cancelBeerOrder(UUID beerOrderId) {
		Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(beerOrderId);
		
		beerOrderOptional.ifPresentOrElse(beerOrder -> {
			sendBeerOrderEvent(beerOrder, BeerOrderEventEnum.CANCEL_ORDER);
		}, () -> log.error("Order Not Found. Id: " + beerOrderId));
	}
	
	private void updateAllocateQty(BeerOrderDto beerOrderDto, BeerOrder allocatedOrder) {
		
		allocatedOrder.getBeerOrderLines().forEach(orderLine -> {
			beerOrderDto.getBeerOrderLines().forEach(orderLineDto -> {
				if(orderLine.getId().equals(orderLineDto.getId()))
					orderLine.setQuantityAllocated(orderLineDto.getQuantityAllocated());
			});
		});
		
		beerOrderRepository.saveAndFlush(allocatedOrder);
	}
	
	// Envía eventos a la máquina de estados
	private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum event) {
		StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = build(beerOrder);
		
		Message<BeerOrderEventEnum> msg = MessageBuilder.withPayload(event)
				.setHeader(ORDER_ID_HEADER, beerOrder.getId())
				.build();
		
		sm.sendEvent(msg);
	}
	
	// Inicializa la máquina de estados
	private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
		StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());
	
		sm.stop();
		
		sm.getStateMachineAccessor().doWithAllRegions(sma -> {
			sma.addStateMachineInterceptor(beerOrderStateChangeInterceptor); // Registramos nuestro interceptor para que actualice el estado de los pedidos de cervezas en la base de datos antes de que se produzca un cambio de estado
			sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
		});
		
		sm.start();
		
		return sm;
	}

}
