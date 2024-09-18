package guru.springframework.msscbrewery.web.mappers;

import org.mapstruct.Mapper;

import guru.springframework.msscbrewery.domain.Customer;
import guru.springframework.msscbrewery.web.model.CustomerDto;

@Mapper
public interface CustomerMapper {
	
	CustomerDto customerToCustomerDto(Customer customer);
	Customer customerDtoToCustomer(CustomerDto customerDto);

}
