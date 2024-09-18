package guru.springframework.msscssm.services;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.msscssm.config.interceptors.PaymentStateChangeInterceptor;
import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
	
	public static final String PAYMENT_ID_HEADER = "payment_id";
	
	private final PaymentRepository paymentRepository;
	private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
	private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

	@Override
	public Payment newPayment(Payment payment) {
		payment.setState(PaymentState.NEW);
		return paymentRepository.save(payment);
	}

	@Transactional
	@Override
	public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
		return sm;
	}
	
	@Transactional
	@Override
	public StateMachine<PaymentState, PaymentEvent> auth(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.AUTHORIZE);
		return sm;
	}

	// Actualmente no se necesita porque el evento "AUTH_APPROVED" puede producirse en la acción "authAction" configurada en la clase de configuración "StateMachineConfig"
	@Transactional
	@Override
	public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.AUTH_APPROVED);
		return sm;
	}

	// Actualmente no se necesita porque el evento "AUTH_DECLINED" puede producirse en la acción "authAction" configurada en la clase de configuración "StateMachineConfig"
	@Transactional
	@Override
	public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		sendEvent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
		return sm;
	}
	
	// Envía eventos a la máquina de estados
	private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
		
		Message<PaymentEvent> msg = MessageBuilder.withPayload(event)
				.setHeader(PAYMENT_ID_HEADER, paymentId)
				.build();
		
		sm.sendEvent(msg);
	}
	
	
	// Inicializa la máquina de estados desde la base de datos
	private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
		
		Payment payment = paymentRepository.getOne(paymentId);
		
		StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
		
		sm.stop();
		
		sm.getStateMachineAccessor().doWithAllRegions(sma -> {
			sma.addStateMachineInterceptor(paymentStateChangeInterceptor); // Registramos nuestro interceptor para que actualice el estado de los pagos en la base de datos antes de que se produzca un cambio de estado
			sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
		});
		
		sm.start();
		
		return sm;
	}

}
