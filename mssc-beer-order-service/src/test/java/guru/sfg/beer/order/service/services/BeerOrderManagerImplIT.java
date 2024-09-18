package guru.sfg.beer.order.service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;

import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.repositories.CustomerRepository;
import guru.sfg.beer.order.service.services.beer.BeerServiceRestTemplateImpl;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.events.AllocateFailureCompensationRequest;
import guru.sfg.brewery.model.events.DeallocateOrderRequest;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@ExtendWith(WireMockExtension.class) // Usamos la extensión WireMock
// Con esta propiedad puesta a false, desactivamos la tarea programada(desactiva la anotación @Scheduled) de la clase "TastingRoomService"
@SpringBootTest(properties = "guru.sfg.beer.order.service.scheduling.enabled=false")
public class BeerOrderManagerImplIT {
	
	@Autowired
	BeerOrderManager beerOrderManager;
	
	@Autowired
	BeerOrderRepository beerOrderRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	WireMockServer wireMockServer;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	Customer testCustomer;
	
	UUID beerId;
	
	
	@TestConfiguration
	static class RestTemplateBuilderProvider {
		
		// Crea un bean de Spring con el servidor WireMock y su configuración
		// Invoca al método "stop" de este bean justo antes de destruirse. Es una acción recomendada por la documentación de WireMock
		@Bean(destroyMethod = "stop") 
		public WireMockServer wireMockServer() {
			WireMockServer server = with(wireMockConfig().port(8083));
			server.start();
			return server;
		}
		
	}
	
	@BeforeEach
	void setUp() {
		testCustomer = customerRepository.save(Customer.builder()
				.customerName("Test Customer")
				.build());
		beerId = UUID.randomUUID();
	}
	
	@Test
	void newToAllocatedTest() throws JsonProcessingException, InterruptedException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		
		AtomicReference<BeerOrder> aBeerOrder = new AtomicReference<>();
		
		beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.ALLOCATED, forundOrder.getOrderStatus());
			
			aBeerOrder.set(forundOrder);
		});
		
		BeerOrder forundOrder = aBeerOrder.get();
		
		assertNotNull(forundOrder);
		forundOrder.getBeerOrderLines().forEach(orderLine ->
			assertEquals(orderLine.getOrderQuantity(), orderLine.getQuantityAllocated()
		));
	}
	
	@Test
	void failedValidationTest() throws JsonProcessingException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		beerOrder.setCustomerRef("fail-validation");
		
		beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, forundOrder.getOrderStatus());
			
		});
	}
	
	@Test
	void failedAllocationTest() throws JsonProcessingException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		beerOrder.setCustomerRef("fail-allocation");
		
		BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.ALLOCATION_EXCEPTION, forundOrder.getOrderStatus());
			
		});
		
		AllocateFailureCompensationRequest request = (AllocateFailureCompensationRequest)jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATE_ORDER_FAILURE_QUEUE);
	
		assertNotNull(request);
		assertThat(request.getBeerOrderId()).isEqualTo(savedBeerOrder.getId());
	}
	
	@Test
	void partialAllocationTest() throws JsonProcessingException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		beerOrder.setCustomerRef("partial-allocation");
		
		beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.PENDING_INVENTORY, forundOrder.getOrderStatus());
			
		});
	}
	
	@Test
	void newToPickedUpTest() throws JsonProcessingException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		
		beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.ALLOCATED, foundOrder.getOrderStatus());
			
		});
		
		beerOrderManager.pickedUpBeerOrder(beerOrder.getId());
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder foundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.PICKED_UP, foundOrder.getOrderStatus());
		});
	}
	
	@Test
	void validationPendingToCancelTest() throws JsonProcessingException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		beerOrder.setCustomerRef("dont-validate");
		
		beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.VALIDATION_PENDING, forundOrder.getOrderStatus());
		});
		
		beerOrderManager.cancelBeerOrder(beerOrder.getId());
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.CANCELLED, forundOrder.getOrderStatus());
		});
	}
	
	@Test
	void allocationPendingToCancelTest() throws JsonProcessingException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		beerOrder.setCustomerRef("dont-allocate");
		
		beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.ALLOCATION_PENDING, forundOrder.getOrderStatus());
		});
		
		beerOrderManager.cancelBeerOrder(beerOrder.getId());
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.CANCELLED, forundOrder.getOrderStatus());
		});
	}
	
	@Test
	void allocatedToCancelTest() throws JsonProcessingException {
		BeerDto beerDto = BeerDto.builder()
				.id(beerId)
				.upc("12345")
				.build();
		
		String uri = UriComponentsBuilder
				.fromUriString(BeerServiceRestTemplateImpl.BEER_PATH_V1)
				.build(beerDto.getUpc())
				.toString();
		
		wireMockServer.stubFor(get(uri)
				.willReturn(okJson(mapper.writeValueAsString(beerDto))));
		
		BeerOrder beerOrder = createBeerOrder();
		
		BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.ALLOCATED, forundOrder.getOrderStatus());
			
		});
		
		beerOrderManager.cancelBeerOrder(beerOrder.getId());
		
		// Para esperar a que los mensajes JMS se procesen, ya que el envío de mensajes al broker de mensajería ActiveMQ es asíncrono, y así lograr que la máquina de estados de pedidos de cerveza esté en el estado esperado
		await().untilAsserted(() -> {
			BeerOrder forundOrder = beerOrderRepository.findById(beerOrder.getId()).get();
			
			assertEquals(BeerOrderStatusEnum.CANCELLED, forundOrder.getOrderStatus());
		});
		
		DeallocateOrderRequest request = (DeallocateOrderRequest)jmsTemplate.receiveAndConvert(JmsConfig.DEALLOCATE_ORDER_QUEUE);
	
		assertNotNull(request);
		assertThat(request.getBeerOrderDto().getId()).isEqualTo(savedBeerOrder.getId());
	}
	
	private BeerOrder createBeerOrder() {
		BeerOrder beerOrder = BeerOrder.builder()
				.customer(testCustomer)
				.build();
		
		Set<BeerOrderLine> lines = new HashSet<>();
		lines.add(BeerOrderLine.builder()
				.beerId(beerId)
				.upc("12345")
				.orderQuantity(1)
				.beerOrder(beerOrder)
				.build());
		
		beerOrder.setBeerOrderLines(lines);
		
		return beerOrder;
	}

}
