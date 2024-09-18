package guru.sfg.beer.order.service.sm;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import lombok.RequiredArgsConstructor;


@EnableStateMachineFactory
@RequiredArgsConstructor
@Configuration
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

	// Nota: Como tenemos muchos beans de Spring que implementan la interfaz "Action<PaymentState, PaymentEvent>", usando los nombres de las clases que implementan dicha interfaz(comenzando en minúscula) en las propiedades, Spring sabe en qué propiedad tiene que inyectar cada uno de estos beans 
	
	private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateOrderAction;
	private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocateOrderAction;
	private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validationFailureAction;
	private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> allocationFailureAction;
	private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> deallocateOrderAction;
	
	// Sobrescribimos este método de la clase padre "StateMachineConfigurerAdapter" para configurar los estados de nuestra máquina de estados de pedidos de cervezas
	@Override
	public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states)
			throws Exception {
		
		states.withStates()
				// Estado inicial
				.initial(BeerOrderStatusEnum.NEW)
				// Todos los estados de nuestra máquina de estados
				.states(EnumSet.allOf(BeerOrderStatusEnum.class))
				// Estados finales
				.end(BeerOrderStatusEnum.DELIVERED)
				.end(BeerOrderStatusEnum.PICKED_UP)
				.end(BeerOrderStatusEnum.CANCELLED)
				.end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
				.end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
				.end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
	}

	// Sobrescribimos este método de la clase padre "StateMachineConfigurerAdapter" para configurar las transiciones entre los estados de nuestra máquina de pedidos de cervezas
	@Override
	public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions)
			throws Exception {
	
		// Si estamos en el estado "NEW" y se produce el evento "VALIDATE_ORDER", nos movemos al estado "VALIDATION_PENDING" y, además, se ejecuta la acción del método "validateOrderAction"
		transitions.withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_PENDING).event(BeerOrderEventEnum.VALIDATE_ORDER).action(validateOrderAction)
			.and()
			// Si estamos en el estado "VALIDATION_PENDING" y se produce el evento "VALIDATION_PASSED", nos movemos al estado "VALIDATED"
			.withExternal().source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATED).event(BeerOrderEventEnum.VALIDATION_PASSED)
			.and()
			// Si estamos en el estado "VALIDATION_PENDING" y se produce el evento "CANCELLED", nos movemos al estado "CANCEL_ORDER"
			.withExternal().source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.CANCELLED).event(BeerOrderEventEnum.CANCEL_ORDER)
			.and()
			// Si estamos en el estado "VALIDATION_PENDING" y se produce el evento "VALIDATION_FAILED", nos movemos al estado "VALIDATION_EXCEPTION" y, además, se ejecuta la acción del método "validationFailureAction"
			.withExternal().source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION).event(BeerOrderEventEnum.VALIDATION_FAILED).action(validationFailureAction)
			.and()
			// Si estamos en el estado "VALIDATED" y se produce el evento "ALLOCATE_ORDER", nos movemos al estado "ALLOCATION_PENDING" y, además, se ejecuta la acción del método "allocateOrderAction"
			.withExternal().source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATION_PENDING).event(BeerOrderEventEnum.ALLOCATE_ORDER).action(allocateOrderAction)
			.and()
			// Si estamos en el estado "VALIDATED" y se produce el evento "CANCELLED", nos movemos al estado "CANCEL_ORDER"
			.withExternal().source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.CANCELLED).event(BeerOrderEventEnum.CANCEL_ORDER)
			.and()
			// Si estamos en el estado "ALLOCATION_PENDING" y se produce el evento "ALLOCATION_SUCCESS", nos movemos al estado "ALLOCATED"
			.withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATED).event(BeerOrderEventEnum.ALLOCATION_SUCCESS)
			.and()
			// Si estamos en el estado "ALLOCATION_PENDING" y se produce el evento "CANCELLED", nos movemos al estado "CANCEL_ORDER"
			.withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.CANCELLED).event(BeerOrderEventEnum.CANCEL_ORDER)
			.and()
			// Si estamos en el estado "ALLOCATION_PENDING" y se produce el evento "ALLOCATION_FAILED", nos movemos al estado "ALLOCATION_EXCEPTION" y, además, se ejecuta la acción del método "allocationFailureAction"
			.withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATION_EXCEPTION).event(BeerOrderEventEnum.ALLOCATION_FAILED).action(allocationFailureAction)
			.and()
			// Si estamos en el estado "ALLOCATION_PENDING" y se produce el evento "ALLOCATION_NO_INVENTORY", nos movemos al estado "PENDING_INVENTORY"
			.withExternal().source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.PENDING_INVENTORY).event(BeerOrderEventEnum.ALLOCATION_NO_INVENTORY)
			.and()
			// Si estamos en el estado "ALLOCATED" y se produce el evento "BEERORDER_PICKED_UP", nos movemos al estado "PICKED_UP"
			.withExternal().source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.PICKED_UP).event(BeerOrderEventEnum.BEERORDER_PICKED_UP)
			.and()
			// Si estamos en el estado "ALLOCATED" y se produce el evento "CANCELLED", nos movemos al estado "CANCEL_ORDER" y, además, se ejecuta la acción del método "deallocateOrderAction"
			.withExternal().source(BeerOrderStatusEnum.ALLOCATED).target(BeerOrderStatusEnum.CANCELLED).event(BeerOrderEventEnum.CANCEL_ORDER).action(deallocateOrderAction);
	}
	
}
