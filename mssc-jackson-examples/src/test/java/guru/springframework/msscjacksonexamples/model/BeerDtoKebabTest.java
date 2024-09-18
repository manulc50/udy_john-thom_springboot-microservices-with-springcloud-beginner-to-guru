package guru.springframework.msscjacksonexamples.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;

@ActiveProfiles("kebab")
@JsonTest
public class BeerDtoKebabTest extends BeerTest{

	@Test
	void testKebab() throws JsonProcessingException {
		BeerDto beerDto = getDto();
		String json = objectMapper.writeValueAsString(beerDto);
		System.out.println(json);
	}

}
