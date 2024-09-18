package guru.springframework.msscbrewery.web.controller;

import static org.mockito.BDDMockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.msscbrewery.services.BeerService;
import guru.springframework.msscbrewery.web.controller.BeerController;
import guru.springframework.msscbrewery.web.model.BeerDto;

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
				.beerName("Beer1")
				.beerStyle("PALE_ALE")
				.upc(123456789012L)
				.build();
	}
	
	@Test
	public void getBeerTest() throws Exception {
		given(beerService.getBeerById(any())).willReturn(validBeer);
		
		mockMvc.perform(get("/api/v1/beer/" + validBeer.getId().toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id",is(validBeer.getId().toString())))
				.andExpect(jsonPath("$.beerName",is("Beer1")));
	}
	
	@Test
	public void createNewBeerTest() throws Exception {
		BeerDto beerDto = validBeer;
		beerDto.setId(null);
		BeerDto storedBeer = BeerDto.builder().id(UUID.randomUUID()).beerName("New Beer").build();
		String beerDtoJson = objectMapper.writeValueAsString(beerDto);
		
		given(beerService.saveNewBeer(any())).willReturn(storedBeer);
		
		mockMvc.perform(post("/api/v1/beer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(beerDtoJson))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void updateBeerTest() throws Exception {
		BeerDto beerDto = validBeer;
		beerDto.setId(null);
		String beerDtoJson = objectMapper.writeValueAsString(beerDto);
		
		mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID())
				.contentType(MediaType.APPLICATION_JSON)
				.content(beerDtoJson))
				.andExpect(status().isNoContent());
		
		then(beerService).should().updateBeer(any(),any());
		
		
		
	}

}
