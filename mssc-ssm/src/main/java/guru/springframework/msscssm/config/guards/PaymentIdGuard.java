package guru.springframework.msscssm.config.guards;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.services.PaymentServiceImpl;


// "Guard" para asegurar que el id del pago está presente en la cabecera "PaymentServiceImpl.PAYMENT_ID_HEADER" del mensaje del contexto de la máquina de estados
// Este id siempre tiene que estar para poder recuperarlo después en el interceptor y así poder actualizar el estado del pago en la base de datos
// La implementación de un "Guard" devuelve true o false. Si es true, se ejecuta la acción asociada al "Guard" y, si es false, no se ejecuta dicha acción

@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {

	@Override
	public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
		return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
	}

}
