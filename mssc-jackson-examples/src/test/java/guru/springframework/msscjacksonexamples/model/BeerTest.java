package guru.springframework.msscjacksonexamples.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BeerTest {
	
	@Autowired
	ObjectMapper objectMapper;
	
	BeerDto getDto() {
		return BeerDto.builder()
				.id(UUID.randomUUID())
				.beerName("BeerName")
				.beerStyle("Ale")
				.createdDate(OffsetDateTime.now())
				.lastUpdatedDate(OffsetDateTime.now())
				.price(new BigDecimal("12.99"))
				.upc(123123123123L)
				.myLocalDate(LocalDate.now())
				.build();
	}
}
