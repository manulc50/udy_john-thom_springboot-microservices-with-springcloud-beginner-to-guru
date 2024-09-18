package guru.springframework.msscbrewery.services.V2;

import java.util.UUID;

import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;

public interface BeerServiceV2 {
	
	public BeerDtoV2 getBeerById(UUID beerId);
	public BeerDtoV2 saveNewBeer(BeerDtoV2 beerDto);
	public void updateBeer(UUID beerId,BeerDtoV2 beerDto);
	public void deleteById(UUID beerId);
}
