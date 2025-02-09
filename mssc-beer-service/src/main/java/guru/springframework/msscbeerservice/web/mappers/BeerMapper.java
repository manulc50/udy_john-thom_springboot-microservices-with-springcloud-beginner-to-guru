package guru.springframework.msscbeerservice.web.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.sfg.brewery.model.BeerDto;


@Mapper(uses = {DateMapper.class})
@DecoratedWith(BeerMapperDecorator.class)
public interface BeerMapper {

	BeerDto beerToBeerDto(Beer beer);
	BeerDto beerToBeerDtoWithInventory(Beer beer);
	Beer beerDtoToBeer(BeerDto beerDto);
}
