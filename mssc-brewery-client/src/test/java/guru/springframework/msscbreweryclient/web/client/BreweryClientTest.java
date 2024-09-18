package guru.springframework.msscbreweryclient.web.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.springframework.msscbreweryclient.web.model.BeerDto;
import guru.springframework.msscbreweryclient.web.model.CustomerDto;


// Nota: Para que estas pruebas funcionen correctamente, tiene que estar previamente ejecutándose la aplicación "mssc-brewery"

@SpringBootTest
public class BreweryClientTest {
	
	@Autowired
	private BreweryClient client;
	
	@Test
	void getBeerByIdTest() {
		BeerDto beerDto = client.getBeerById(UUID.randomUUID());
		
		assertNotNull(beerDto);
	}
	
	@Test
	void saveNewBeerTest() {
		BeerDto beerDto = BeerDto.builder()
				.beerName("New Beer")
				.beerStyle("Pale Ale")
				.upc(0631234200036L)
				.build();
		
		URI uri = client.saveNewBeer(beerDto);
		
		assertNotNull(uri);
		
		System.out.println(uri.toString());
	}
	
	@Test
	void updateBeerTest() {
		BeerDto beerDto = BeerDto.builder()
				.beerName("New Beer")
				.beerStyle("Pale Ale")
				.upc(0631234200036L)
				.build();
		
		client.updateBeer(UUID.randomUUID(), beerDto);
	}
	
	@Test
	void deleteBeerTest() {
		client.deleteBeer(UUID.randomUUID());
	}
	
	@Test
	void getCustomerByIdTest() {
		CustomerDto customerDto = client.getCustomerById(UUID.randomUUID());
		
		assertNotNull(customerDto);
	}
	
	@Test
	void saveNewCustomerTest() {
		CustomerDto customerDto = CustomerDto.builder()
				.customerName("New Customer")
				.build();
		
		URI uri = client.saveNewCustomer(customerDto);
		
		assertNotNull(uri);
		
		System.out.println(uri.toString());
	}
	
	@Test
	void saveNewCustomerV2Test() {
		CustomerDto customerDto = CustomerDto.builder()
				.customerName("New Customer")
				.build();
		
		CustomerDto savedDto = client.saveNewCustomerV2(customerDto);
		
		assertNotNull(savedDto);
	}
	
	@Test
	void updateCustomerTest() {
		CustomerDto customerDto = CustomerDto.builder()
				.customerName("New Customer")
				.build();
		
		client.updateCustomer(UUID.randomUUID(), customerDto);
	}
	
	@Test
	void deleteCustomerTest() {
		client.deleteCustomer(UUID.randomUUID());
	}

}
