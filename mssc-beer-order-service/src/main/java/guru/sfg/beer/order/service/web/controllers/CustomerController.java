package guru.sfg.beer.order.service.web.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import guru.sfg.beer.order.service.services.CustomerService;
import guru.sfg.brewery.model.CustomerPagedList;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
	
	private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;
	
	@Autowired
	private CustomerService customerService;
	
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CustomerPagedList listCustomers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
    		@RequestParam(value = "pageSize", required = false) Integer pageSize){
    	
    	if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return customerService.listCustomers(PageRequest.of(pageNumber, pageSize));
    }

}
