package guru.springframework.msscbeerservice.services.inventory;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Disabled // Anotación para que los tests de esta clase sean ignorados y no se ejecuten
@SpringBootTest
public class BeerInventoryServiceRestTemplateImplTest {
	
	@Autowired
	private BeerInventoryService beerInventoryService;
	
	@Test
	void getOnHandInventory() {
//		Integer qoh = beerInventoryService.getOnHandInventory(BeerLoader.BEER_1_UUID);
//		System.out.println(qoh);
	}

}
