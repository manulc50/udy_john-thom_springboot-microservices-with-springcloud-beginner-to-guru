package guru.springframework.msscbrewery.web.mappers;

import org.mapstruct.Mapper;

import guru.springframework.msscbrewery.domain.Beer;
import guru.springframework.msscbrewery.web.model.BeerDto;

@Mapper(uses = {DateMapper.class})
public interface BeerMapper {
	
	BeerDto beerToBeerDto(Beer beer);
	Beer beerDtoToBeer(BeerDto beerDto);
}
