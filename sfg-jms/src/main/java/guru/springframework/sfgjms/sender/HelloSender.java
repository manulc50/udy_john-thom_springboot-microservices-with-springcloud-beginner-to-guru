package guru.springframework.sfgjms.sender;

import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class HelloSender {
	
	private final JmsTemplate jmsTemplate;
	private final ObjectMapper objectMapper;
	
	@Scheduled(fixedRate = 2000)
	public void sendMessage() {
		// System.out.println("I'm sending a message");
		
		HelloWorldMessage message = HelloWorldMessage.builder()
				.id(UUID.randomUUID())
				.message("Hello World!")
				.build();
		
		// El método "convertAndSend" utiliza por debajo el bean de Spring que hemos configurado en la clase de configuración de Spring "JmsConfig" para realizar la conversión de los mensajes antes de ser enviados a la cola del broker ActiveMQ
		jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, message);
		
		// System.out.println("Message Sent!");
	}
	
	@Scheduled(fixedRate = 2000)
	public void sendAndReceiveMessage() throws JMSException {
		
		HelloWorldMessage message = HelloWorldMessage.builder()
				.id(UUID.randomUUID())
				.message("Hello")
				.build();
		
		// El método "sendAndReceive", a diferencia del método "convertAndSend", utiliza su propio convertidor de mensajes en lugar del bean de Spring que hemos configurado en la clase de configuración de Spring "JmsConfig"
		// Para ello, se implementa la interfaz "MessageCreator" con la implementación deseada, que en nuestro caso, es la misma que utiliza el bean de Spring que hemos configurado en la clase de configuración de Spring "JmsConfig"
		Message receiveMsg = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RCV_QUEUE, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message helloMessage = null;
				try {
					helloMessage = session.createTextMessage(objectMapper.writeValueAsString(message));
					helloMessage.setStringProperty("_type","guru.springframework.sfgjms.model.HelloWorldMessage");
					
					System.out.println("Sending Hello");
					
				}
				catch (JsonProcessingException e) {
					throw new JMSException("boom");
				}
				
				return helloMessage;
				
			}
		});
		
		System.out.println(receiveMsg.getBody(String.class));
	}

}
