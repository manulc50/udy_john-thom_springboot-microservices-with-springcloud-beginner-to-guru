package guru.springframework.msscbeerservice.web.mappers;

import org.springframework.beans.factory.annotation.Autowired;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.services.inventory.BeerInventoryService;
import guru.sfg.brewery.model.BeerDto;

public class BeerMapperDecorator implements BeerMapper{
	
	private BeerInventoryService beerInventoryService;
	private BeerMapper mapper;
	
	@Autowired
	public void setBeerInventoryService(BeerInventoryService beerInventoryService) {
		this.beerInventoryService = beerInventoryService;
	}
	
	@Autowired
	public void setMapper(BeerMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public BeerDto beerToBeerDto(Beer beer) {
		return  mapper.beerToBeerDto(beer);
	}
	
	@Override
	public BeerDto beerToBeerDtoWithInventory(Beer beer) {
		BeerDto beerDto =  mapper.beerToBeerDto(beer);
		beerDto.setQuantityOnHand(beerInventoryService.getOnHandInventory(beer.getId()));
		return beerDto;
	}

	@Override
	public Beer beerDtoToBeer(BeerDto beerDto) {
		return mapper.beerDtoToBeer(beerDto);
	}
	
}
