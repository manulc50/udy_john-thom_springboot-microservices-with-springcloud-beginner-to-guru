package guru.sfg.beer.order.service.services.testcomponents;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.brewery.model.events.ValidateBeerOrderRequest;
import guru.sfg.brewery.model.events.ValidateBeerOrderResponse;
import lombok.RequiredArgsConstructor;

// Este componente de Spring simula la recepción del mensaje "VALIDATE_ORDER_QUEUE" desde el broker de mensajería "ActiveMQ" por parte del microservicio "mssc-beer-service" para realizar la valicación de un pedido de cervezas

@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {
	
	private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message<ValidateBeerOrderRequest> msg){
        boolean isValid = true;
        boolean sendResponse = true;

        ValidateBeerOrderRequest request = msg.getPayload();
 
        if (request.getBeerOrderDto().getCustomerRef() != null) {
        	//condition to fail validation 
            if (request.getBeerOrderDto().getCustomerRef().equals("fail-validation"))
                isValid = false;
            //condition to test cancel order
            else if (request.getBeerOrderDto().getCustomerRef().equals("dont-validate"))
                sendResponse = false;
        }

        // Si estamos probando los test que cancelan un pedido de cervezas, no se envía este mensaje para evitar que la máquina de estados siga cambiando de estado, ya que se va a cancelar el pedido
        if (sendResponse) {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE,
                    ValidateBeerOrderResponse.builder()
                            .isValid(isValid)
                            .beerOrderId(request.getBeerOrderDto().getId())
                            .build());
        }
    }

}
