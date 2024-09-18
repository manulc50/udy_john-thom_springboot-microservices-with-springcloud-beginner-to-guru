package guru.sfg.beer.order.service.services.beer;

import java.util.Optional;

import guru.sfg.brewery.model.BeerDto;

public interface BeerService {
	public Optional<BeerDto> getBeerByUpc(String upc);
}
