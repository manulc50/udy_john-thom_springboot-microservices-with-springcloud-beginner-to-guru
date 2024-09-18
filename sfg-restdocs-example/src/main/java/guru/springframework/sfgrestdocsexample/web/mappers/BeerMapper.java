package guru.springframework.sfgrestdocsexample.web.mappers;

import org.mapstruct.Mapper;

import guru.springframework.sfgrestdocsexample.domain.Beer;
import guru.springframework.sfgrestdocsexample.web.model.BeerDto;


@Mapper(uses = {DateMapper.class})
public interface BeerMapper {

	BeerDto beerToBeerDto(Beer beer);
	Beer beerDtoToBeer(BeerDto beerDto);
}
