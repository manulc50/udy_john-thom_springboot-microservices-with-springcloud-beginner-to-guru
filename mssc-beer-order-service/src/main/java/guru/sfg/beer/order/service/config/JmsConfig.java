package guru.sfg.beer.order.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JmsConfig {
	
	public static final String VALIDATE_ORDER_QUEUE = "validate_order";
	public static final String VALIDATE_ORDER_RESPONSE_QUEUE = "validate_order_response";
	public static final String ALLOCATE_ORDER_QUEUE = "allocate_order";
	public static final String ALLOCATE_ORDER_RESPONSE_QUEUE = "allocate_order_response";
	public static final String ALLOCATE_ORDER_FAILURE_QUEUE = "allocate_order_failure";
	public static final String DEALLOCATE_ORDER_QUEUE = "deallocate_order";
	
	
	// This mehtod serialize message content to Json using TextMessage
	@Bean
	public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		converter.setObjectMapper(objectMapper);
		
		return converter;
	}

}
