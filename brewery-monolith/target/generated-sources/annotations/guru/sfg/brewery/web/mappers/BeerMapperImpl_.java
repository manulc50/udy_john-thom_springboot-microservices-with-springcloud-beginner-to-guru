package guru.sfg.brewery.web.mappers;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.Beer.BeerBuilder;
import guru.sfg.brewery.web.model.BeerDto;
import guru.sfg.brewery.web.model.BeerDto.BeerDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-05-06T14:06:24+0200",
    comments = "version: 1.3.0.Final, compiler: Eclipse JDT (IDE) 3.28.0.v20211117-1416, environment: Java 17.0.1 (Eclipse Adoptium)"
)
@Component
@Qualifier("delegate")
public class BeerMapperImpl_ implements BeerMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public BeerDto beerToBeerDto(Beer beer) {
        if ( beer == null ) {
            return null;
        }

        BeerDtoBuilder beerDto = BeerDto.builder();

        beerDto.beerName( beer.getBeerName() );
        beerDto.beerStyle( beer.getBeerStyle() );
        beerDto.createdDate( dateMapper.asOffsetDateTime( beer.getCreatedDate() ) );
        beerDto.id( beer.getId() );
        beerDto.lastModifiedDate( dateMapper.asOffsetDateTime( beer.getLastModifiedDate() ) );
        beerDto.price( beer.getPrice() );
        beerDto.upc( beer.getUpc() );
        if ( beer.getVersion() != null ) {
            beerDto.version( beer.getVersion().intValue() );
        }

        return beerDto.build();
    }

    @Override
    public Beer beerDtoToBeer(BeerDto beerDto) {
        if ( beerDto == null ) {
            return null;
        }

        BeerBuilder beer = Beer.builder();

        beer.beerName( beerDto.getBeerName() );
        beer.beerStyle( beerDto.getBeerStyle() );
        beer.createdDate( dateMapper.asTimestamp( beerDto.getCreatedDate() ) );
        beer.id( beerDto.getId() );
        beer.lastModifiedDate( dateMapper.asTimestamp( beerDto.getLastModifiedDate() ) );
        beer.price( beerDto.getPrice() );
        beer.upc( beerDto.getUpc() );
        if ( beerDto.getVersion() != null ) {
            beer.version( beerDto.getVersion().longValue() );
        }

        return beer.build();
    }
}
