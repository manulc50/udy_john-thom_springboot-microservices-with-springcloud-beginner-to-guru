package guru.sfg.beer.order.service.services;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.web.mappers.CustomerMapper;
import guru.sfg.brewery.model.CustomerPagedList;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
	
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;

	@Transactional(readOnly = true)
	@Override
	public CustomerPagedList listCustomers(Pageable pageable) {
		Page<Customer> cutomerPage = customerRepository.findAll(pageable);

        return new CustomerPagedList(cutomerPage.stream()
                .map(customerMapper::customerToDto).collect(Collectors.toList()),
                PageRequest.of(cutomerPage.getPageable().getPageNumber(),
                		cutomerPage.getPageable().getPageSize()),
                		cutomerPage.getTotalElements());
	}

}
