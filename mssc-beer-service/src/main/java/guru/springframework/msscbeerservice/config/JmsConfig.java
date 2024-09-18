package guru.springframework.msscbeerservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class JmsConfig {
	
	public static final String BREWING_REQUEST_QUEUE = "brewing-request";
	public static final String NEW_INVENTORY_QUEUE = "new-inventory";
	public static final String VALIDATE_ORDER_QUEUE = "validate_order";
	public static final String VALIDATE_ORDER_RESPONSE_QUEUE = "validate_order_response";
	
	// This mehtod serialize message content to Json using TextMessage
	@Autowired
	@Bean
	public MessageConverter messageConverter(ObjectMapper objectMapper) {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		converter.setObjectMapper(objectMapper);
		return converter;
	}

}
