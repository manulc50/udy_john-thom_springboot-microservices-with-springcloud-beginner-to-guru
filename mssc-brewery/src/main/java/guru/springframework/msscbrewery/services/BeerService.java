package guru.springframework.msscbrewery.services;

import java.util.UUID;

import guru.springframework.msscbrewery.web.model.BeerDto;

public interface BeerService {
	
	public BeerDto getBeerById(UUID beerId);
	public BeerDto saveNewBeer(BeerDto beerDto);
	public void updateBeer(UUID beerId,BeerDto beerDto);
	public void deleteById(UUID beerId);

}
