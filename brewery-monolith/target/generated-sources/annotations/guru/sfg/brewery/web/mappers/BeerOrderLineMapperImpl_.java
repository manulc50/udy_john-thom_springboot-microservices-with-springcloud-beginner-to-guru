package guru.sfg.brewery.web.mappers;

import guru.sfg.brewery.domain.BeerOrderLine;
import guru.sfg.brewery.domain.BeerOrderLine.BeerOrderLineBuilder;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto.BeerOrderLineDtoBuilder;
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
public class BeerOrderLineMapperImpl_ implements BeerOrderLineMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        if ( line == null ) {
            return null;
        }

        BeerOrderLineDtoBuilder beerOrderLineDto = BeerOrderLineDto.builder();

        beerOrderLineDto.createdDate( dateMapper.asOffsetDateTime( line.getCreatedDate() ) );
        beerOrderLineDto.id( line.getId() );
        beerOrderLineDto.lastModifiedDate( dateMapper.asOffsetDateTime( line.getLastModifiedDate() ) );
        beerOrderLineDto.orderQuantity( line.getOrderQuantity() );
        if ( line.getVersion() != null ) {
            beerOrderLineDto.version( line.getVersion().intValue() );
        }

        return beerOrderLineDto.build();
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
        if ( dto == null ) {
            return null;
        }

        BeerOrderLineBuilder beerOrderLine = BeerOrderLine.builder();

        beerOrderLine.createdDate( dateMapper.asTimestamp( dto.getCreatedDate() ) );
        beerOrderLine.id( dto.getId() );
        beerOrderLine.lastModifiedDate( dateMapper.asTimestamp( dto.getLastModifiedDate() ) );
        beerOrderLine.orderQuantity( dto.getOrderQuantity() );
        if ( dto.getVersion() != null ) {
            beerOrderLine.version( dto.getVersion().longValue() );
        }

        return beerOrderLine.build();
    }
}
