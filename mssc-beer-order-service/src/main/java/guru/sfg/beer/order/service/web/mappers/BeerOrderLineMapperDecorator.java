package guru.sfg.beer.order.service.web.mappers;

import org.springframework.beans.factory.annotation.Autowired;

import guru.sfg.beer.order.service.domain.BeerOrderLine;
import guru.sfg.beer.order.service.services.beer.BeerService;
import guru.sfg.brewery.model.BeerOrderLineDto;

public class BeerOrderLineMapperDecorator implements BeerOrderLineMapper{
	
	private BeerService beerService;
	private BeerOrderLineMapper mapper;
	
	@Autowired
	public void setMapper(BeerOrderLineMapper mapper) {
		this.mapper = mapper;
	}
	
	@Autowired
	public void setBeerService(BeerService beerService) {
		this.beerService = beerService;
	}

	@Override
	public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
		BeerOrderLineDto beerOrderLineDto = mapper.beerOrderLineToDto(line);
		beerService.getBeerByUpc(line.getUpc()).ifPresent(beerDto -> {
			beerOrderLineDto.setBeerId(beerDto.getId());
			beerOrderLineDto.setBeerName(beerDto.getBeerName());
			beerOrderLineDto.setUpc(beerDto.getUpc());
			beerOrderLineDto.setBeerStyle(beerDto.getBeerStyle());
			beerOrderLineDto.setPrice(beerDto.getPrice());
		});
		
		return beerOrderLineDto;
	}

	@Override
	public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
		return mapper.dtoToBeerOrderLine(dto);
	}

}
