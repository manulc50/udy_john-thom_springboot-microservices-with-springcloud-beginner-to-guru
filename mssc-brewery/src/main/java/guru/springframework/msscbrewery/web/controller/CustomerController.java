package guru.springframework.msscbrewery.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
	
	private final CustomerService customerService;
	
	CustomerController(CustomerService customerService){
		this.customerService = customerService;
	}
	
	@GetMapping("/{customerId}")
	public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID customerId){
		return ResponseEntity.ok().body(this.customerService.getCustormerById(customerId));
	}
	
	@PostMapping
	public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto customerDto){
		CustomerDto customerStored = this.customerService.saveNewCustomer(customerDto);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location","/api/v1/customer/" + customerStored.getId());
		return new ResponseEntity<CustomerDto>(customerStored,headers,HttpStatus.CREATED);
	}
	
	@PutMapping("/{customerId}")
	public ResponseEntity<Void> updateCustomer(@PathVariable UUID customerId,@Valid @RequestBody CustomerDto customerDto) {
		customerService.updateCustomer(customerId, customerDto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{customerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCustomer(@PathVariable UUID customerId) {
		this.customerService.deleteById(customerId);
	}

}
