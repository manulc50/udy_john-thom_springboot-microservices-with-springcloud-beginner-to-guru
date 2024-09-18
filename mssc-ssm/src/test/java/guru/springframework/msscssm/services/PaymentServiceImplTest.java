package guru.springframework.msscssm.services;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;

@SpringBootTest
public class PaymentServiceImplTest {

	@Autowired
	PaymentService paymentService;
	
	@Autowired
	PaymentRepository paymentRepository;
	
	Payment payment;
	
	@BeforeEach
	void setUp() {
		payment = Payment.builder()
				.amount(new BigDecimal(12.99))
				.build();
	}
	
	
	// Por defecto, un test anotado con la anotación @Transactional hace rollback de todas las acciones que se han hecho sobre la base de datos al finalizar ese test
	@Transactional
	@Test
	void preAuthTest() {
		Payment savedPayment = paymentService.newPayment(payment);
		
		System.out.println("Should be NEW");
		System.out.println(savedPayment.getState());
		
		StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
		
		Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());
		
		System.out.println("Should be PRE_AUTH or PRE_AUTH_ERROR");
		System.out.println(sm.getState().getId());
		
		System.out.println(preAuthedPayment);
	}
	
	// Por defecto, un test anotado con la anotación @Transactional hace rollback de todas las acciones que se han hecho sobre la base de datos al finalizar ese test
	@Transactional
	@RepeatedTest(10)
	void authTest() {
		Payment savedPayment = paymentService.newPayment(payment);
		
		StateMachine<PaymentState, PaymentEvent> preAuthSM = paymentService.preAuth(savedPayment.getId());

		if(preAuthSM.getState().getId() == PaymentState.PRE_AUTH) {
			System.out.println("Payment is Pre Authorized");
			
			StateMachine<PaymentState, PaymentEvent> authSM = paymentService.auth(savedPayment.getId());
		
			System.out.println("Result of Auth: " + authSM.getState().getId());
		}
		else
			System.out.println("Payment failed pre-auth...");
	}
	
}
