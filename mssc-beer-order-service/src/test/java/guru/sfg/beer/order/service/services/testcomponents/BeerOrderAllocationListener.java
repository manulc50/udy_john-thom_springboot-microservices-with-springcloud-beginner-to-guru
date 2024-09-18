package guru.sfg.beer.order.service.services.testcomponents;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResponse;
import lombok.RequiredArgsConstructor;

// Este componente de Spring simula la recepción del mensaje "ALLOCATE_ORDER_QUEUE" desde el broker de mensajería "ActiveMQ" por parte del microservicio "mssc-beer-inventory-service" para realizar el abastecimiento de un pedido de cervezas

@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {
	
	private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void list(Message<AllocateOrderRequest> msg) {
    	boolean allocationError = false;
    	boolean pendingInventory = false;
    	boolean sendResponse = true;
    	
    	AllocateOrderRequest request = msg.getPayload();
    	
    	
        if (request.getBeerOrderDto().getCustomerRef() != null) {
        	//condition to fail allocation
        	if(request.getBeerOrderDto().getCustomerRef().equals("fail-allocation"))
        		allocationError = true;
	        //set pending inventory
        	else if(request.getBeerOrderDto().getCustomerRef().equals("partial-allocation"))
	        	pendingInventory = true;
	        //condition to test cancel order
            else if(request.getBeerOrderDto().getCustomerRef().equals("dont-allocate"))
                sendResponse = false;
        }
    	
        final boolean isPendingInventory = pendingInventory;
    	request.getBeerOrderDto().getBeerOrderLines().forEach(orderLine -> {
    		// No toda la cantidad solicitada se ha proporcionado por falta de inventario
    		if(isPendingInventory)
    			orderLine.setQuantityAllocated(orderLine.getOrderQuantity() - 1);
    		// Happy path -> Toda la cantidad solicitada se ha proporcionado
    		else
    			orderLine.setQuantityAllocated(orderLine.getOrderQuantity());
    	});
	    	
    	// Si estamos probando los test que cancelan un pedido de cervezas, no se envía este mensaje para evitar que la máquina de estados siga cambiando de estado, ya que se va a cancelar el pedido
        if (sendResponse) {	
	    	jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
	                AllocateOrderResponse.builder()
	                        .beerOrderDto(request.getBeerOrderDto())
	                        .allocationError(allocationError)
	                        .pendingInventory(pendingInventory)
	                        .build());
        }
    }

}
