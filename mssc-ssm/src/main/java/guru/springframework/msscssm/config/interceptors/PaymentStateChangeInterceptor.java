package guru.springframework.msscssm.config.interceptors;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;
import guru.springframework.msscssm.services.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;

// Interceptor de la máquina de estados que actualiza el estado de los pagos en la base de datos antes de que se produzca un cambio de estado

@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

	private final PaymentRepository paymentRespository;

	@Override
	public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
			Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine,
			StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
		
		Optional.ofNullable(message)
			.ifPresent(msg -> Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
					.ifPresent(paymentId -> {
		
						Payment payment = paymentRespository.getOne(paymentId);
						payment.setState(state.getId());
						paymentRespository.save(payment);
					}));
	}
	
	
	
}
