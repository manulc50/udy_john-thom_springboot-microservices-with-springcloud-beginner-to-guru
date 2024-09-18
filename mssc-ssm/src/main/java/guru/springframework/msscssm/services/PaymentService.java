package guru.springframework.msscssm.services;

import org.springframework.statemachine.StateMachine;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;

public interface PaymentService {
	
	public Payment newPayment(Payment payment);
	
	public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
	
	public StateMachine<PaymentState, PaymentEvent> auth(Long paymentId);
	
	// Actualmente no se necesita porque el evento "AUTH_APPROVED" puede producirse en la acción "authAction" configurada en la clase de configuración "StateMachineConfig"
	public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);
	
	// Actualmente no se necesita porque el evento "AUTH_DECLINED" puede producirse en la acción "authAction" configurada en la clase de configuración "StateMachineConfig"
	public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);

}
