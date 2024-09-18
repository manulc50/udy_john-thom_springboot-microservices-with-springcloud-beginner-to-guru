package guru.springframework.msscbrewery.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import guru.springframework.msscbrewery.web.model.CustomerDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService{

	@Override
	public CustomerDto getCustormerById(UUID customerId) {
		return CustomerDto.builder()
				.id(UUID.randomUUID())
				.customerName("John Doe")
				.build();
	}

	@Override
	public CustomerDto saveNewCustomer(CustomerDto customerDto) {
		return CustomerDto.builder()
				.id(UUID.randomUUID())
				.build();
	}

	@Override
	public void updateCustomer(UUID customerId, CustomerDto customerDto) {
		// TODO Auto-generated method stub
		log.debug("Updating a customer...");
		
	}

	@Override
	public void deleteById(UUID customerId) {
		// TODO Auto-generated method stub
		log.debug("Deleting a customer...");
	}

}
