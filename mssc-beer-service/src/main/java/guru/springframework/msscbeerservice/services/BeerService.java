package guru.springframework.msscbeerservice.services;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;

import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerPagedList;
import guru.sfg.brewery.model.BeerStyleEnum;

public interface BeerService {
	public BeerPagedList listBeers(String beerName,BeerStyleEnum beerStyle,Boolean showInventoryOnHand,PageRequest pageRequest);
	public BeerDto getBeerById(UUID beerId,Boolean showInventoryOnHand);
	public BeerDto getBeerByUpc(String beerUpc,Boolean showInventoryOnHand);
	public BeerDto saveNewBeer(BeerDto beerDto);
	public BeerDto updateBeer(UUID beerId,BeerDto beerDto);
	public void deleteBeerById(UUID beerId);

}
