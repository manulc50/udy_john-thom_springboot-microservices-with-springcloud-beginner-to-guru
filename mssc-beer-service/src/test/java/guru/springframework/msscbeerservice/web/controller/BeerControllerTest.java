package guru.springframework.msscbeerservice.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.msscbeerservice.bootstrap.BeerLoader;
import guru.springframework.msscbeerservice.services.BeerService;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerStyleEnum;

@WebMvcTest(BeerController.class)
public class BeerControllerTest {
	
	@MockBean
	private BeerService beerService;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private BeerDto validBeer;
	
	@BeforeEach
	public void setUp() {
		validBeer = BeerDto.builder().id(UUID.randomUUID())
				.beerName("My Beer")
				.beerStyle(BeerStyleEnum.ALE)
				.price(new BigDecimal(2.99))
				.upc(BeerLoader.BEER_1_UPC)
				.build();
	}
	
	@Test
	void getBeerByIdTest() throws Exception {
		given(beerService.getBeerById(any(),anyBoolean())).willReturn(validBeer);
		
		mockMvc.perform(get("/api/v1/beer/" + validBeer.getId().toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id",is(validBeer.getId().toString())))
				.andExpect(jsonPath("$.beerName",is("My Beer")));
	}
	
	@Test
	void saveNewBeerTest() throws Exception {
		BeerDto beerDto = validBeer;
		beerDto.setId(null);
		BeerDto storedBeer = BeerDto.builder().id(UUID.randomUUID()).beerName("New Beer").build();
		String beerDtoJson = objectMapper.writeValueAsString(beerDto);
		
		given(beerService.saveNewBeer(any())).willReturn(storedBeer);
		
		mockMvc.perform(post("/api/v1/beer/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(beerDtoJson))
				.andExpect(status().isCreated());	
	}
	
	@Test
	void updateBeerByIdTest() throws Exception {
		BeerDto beerDto = validBeer;
		beerDto.setId(null);
		String beerDtoJson = objectMapper.writeValueAsString(beerDto);
		
		mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(beerDtoJson))
				.andExpect(status().isNoContent());
		
		then(beerService).should().updateBeer(any(),any());
	}

}
