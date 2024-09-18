package guru.springframework.msscjacksonexamples.model;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import com.fasterxml.jackson.core.JsonProcessingException;

@JsonTest
public class BeerDtoTest extends BeerTest{
	
	@Test
	void testSerializeDto() throws JsonProcessingException {
		BeerDto beerDto = getDto();
		String jsonString = objectMapper.writeValueAsString(beerDto);
		System.out.println(jsonString);
	}
	
	@Test
	void testDeserialize() throws IOException{
		String json = "{\"beerName\":\"BeerName\",\"beerStyle\":\"Ale\",\"upc\":123123123123,\"price\":\"12.99\",\"createdDate\":\"2020-08-02T00:24:28+0200\",\"lastUpdatedDate\":\"2020-08-02T00:24:28.2140616+02:00\",\"myLocalDate\":\"20200802\",\"beerId\":\"aa878255-1332-4149-92f9-2e7fc0ea9334\"}";
		BeerDto beerDto = objectMapper.readValue(json,BeerDto.class);
		System.out.println(beerDto);
	}

}
