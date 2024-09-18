package guru.sfg.beer.order.service.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.brewery.model.CustomerDto;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
	
	@Mapping(target = "name", source = "customer.customerName")
	CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(CustomerDto dto);

}
