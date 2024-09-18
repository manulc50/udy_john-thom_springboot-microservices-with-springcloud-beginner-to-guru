package guru.springframework.msscssm.config;

import java.util.EnumSet;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
	
	// Nota: Como tenemos muchos beans de Spring que implementan la interfaz "Action<PaymentState, PaymentEvent>", usando los nombres de las clases que implementan dicha interfaz(comenzando en minúscula) en las propiedades, Spring sabe en qué propiedad tiene que inyectar cada uno de estos beans 
	
	private final Action<PaymentState, PaymentEvent> authAction;
	private final Action<PaymentState, PaymentEvent> authApprovedAction;
	private final Action<PaymentState, PaymentEvent> authDeclinedAction;
	private final Action<PaymentState, PaymentEvent> preAuthAction;
	private final Action<PaymentState, PaymentEvent> preAuthApprovedAction;
	private final Action<PaymentState, PaymentEvent> preAuthDeclinedAction;
	private final Guard<PaymentState, PaymentEvent> paymentIdGuard;
	
	// Sobrescribimos este método de la clase padre "StateMachineConfigurerAdapter" para configurar los estados de nuestra máquina de estados de pagos
	@Override
	public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
		
		states.withStates()
				// Estado inicial
				.initial(PaymentState.NEW)
				// Todos los estados de nuestra máquina de estados
				.states(EnumSet.allOf(PaymentState.class))
				// Estados finales
				.end(PaymentState.AUTH)
				.end(PaymentState.PRE_AUTH_ERROR)
				.end(PaymentState.AUTH_ERROR);
	}

	// Sobrescribimos este método de la clase padre "StateMachineConfigurerAdapter" para configurar las transiciones entre los estados de nuestra máquina de estados de pagos
	@Override
	public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
		
		// Si estamos en el estado "NEW" y se produce el evento "PRE_AUTHORIZE", seguimos en el estado "NEW" y, además, se ejecuta la acción del método "preAuthAction"
		// Sólo si el "Guard" del método "paymentIdGuard" devuelve true, se ejecuta la acción del método "preAuthAction"
		transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE).action(preAuthAction).guard(paymentIdGuard)
			.and()
			// Si estamos en el estado "NEW" y se produce el evento "PRE_AUTH_APPROVED", nos movemos al estado "PRE_AUTH"
			.withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED).action(preAuthApprovedAction)
			.and()
			// Si estamos en el estado "NEW" y se produce el evento "PRE_AUTH_DECLINED", nos movemos al estado "PRE_AUTH_ERROR"
			.withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED).action(preAuthDeclinedAction)
			.and()
			// Si estamos en el estado "PRE_AUTH" y se produce el evento "AUTHORIZE", nos movemos al estado "PRE_AUTH" y, además, se ejecuta la acción del método "authAction"
			.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE).action(authAction)
			.and()
			// Si estamos en el estado "PRE_AUTH" y se produce el evento "AUTH_APPROVED", nos movemos al estado "AUTH"
			.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED).action(authApprovedAction)
			.and()
			// Si estamos en el estado "PRE_AUTH" y se produce el evento "AUTH_DECLINED", nos movemos al estado "AUTH_ERROR"
			.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED).action(authDeclinedAction);
	}

	// Sobrescribimos este método de la clase padre "StateMachineConfigurerAdapter" para configurar el logging usando "State Change Listeners"
	@Override
	public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
		
		StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
			// Sobrescribimos este método para mostrar por consola(nivel de logging INFO) los cambios de estado que se producen
			@Override
			public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
				log.info(String.format("stateChanged(from: %s, to: %s)", from , to ));
			}
			
		};
		
		config.withConfiguration().listener(adapter);
	}
	
	// "Guard" para asegurar que el id del pago está presente en la cabecera "PaymentServiceImpl.PAYMENT_ID_HEADER" del mensaje del contexto de la máquina de estados
	// Este id siempre tiene que estar para poder recuperarlo después en el interceptor y así poder actualizar el estado del pago en la base de datos
	// La implementación de un "Guard" devuelve true o false. Si es true, se ejecuta la acción asociada al "Guard" y, si es false, no se ejecuta dicha acción
	/*private Guard<PaymentState, PaymentEvent> paymentIdGuard() {
		return context -> context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
	}*/
	
	// Acción a realizar cuando la máquina de estados está en el estado "NEW" y se produce el evento "PRE_AUTHORIZE"
	/*private Action<PaymentState, PaymentEvent> preAuthAction() {
		return context -> {
			System.out.println("Pre Auth was called!!");
			
			if(new Random().nextInt(10) < 8) {
				System.out.println("Pre Auth Approved!");
				
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}
			else {
				System.out.println("Pre Auth Declined! No Credit!!!");
				
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}
			
		};
	}*/
	
	// Acción a realizar cuando la máquina de estados está en el estado "PRE_AUTH" y se produce el evento "AUTHORIZE"
	/*private Action<PaymentState, PaymentEvent> authAction() {
		return context -> {
			System.out.println("Auth was called!!");
			
			if(new Random().nextInt(10) < 8) {
				System.out.println("Auth Approved!");
				
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}
			else {
				System.out.println("Auth Declined! No Credit!!!");
				
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}
			
		};
	}*/
	
}
