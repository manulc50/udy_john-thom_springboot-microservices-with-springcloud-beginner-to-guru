package guru.springframework.msscbrewery.services;

import java.util.UUID;

import guru.springframework.msscbrewery.web.model.CustomerDto;

public interface CustomerService {
	
	public CustomerDto getCustormerById(UUID customerId);
	public CustomerDto saveNewCustomer(CustomerDto customerDto);
	public void updateCustomer(UUID customerId,CustomerDto customerDto);
	public void deleteById(UUID customerId);

}
