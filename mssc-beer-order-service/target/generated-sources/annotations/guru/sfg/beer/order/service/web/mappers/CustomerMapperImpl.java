package guru.sfg.beer.order.service.web.mappers;

import guru.sfg.beer.order.service.domain.Customer;
import guru.sfg.beer.order.service.domain.Customer.CustomerBuilder;
import guru.sfg.brewery.model.CustomerDto;
import guru.sfg.brewery.model.CustomerDto.CustomerDtoBuilder;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-18T02:38:46+0200",
    comments = "version: 1.3.0.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 17.0.12 (Eclipse Adoptium)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public CustomerDto customerToDto(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDtoBuilder customerDto = CustomerDto.builder();

        customerDto.name( customer.getCustomerName() );
        customerDto.createdDate( dateMapper.asOffsetDateTime( customer.getCreatedDate() ) );
        customerDto.id( customer.getId() );
        customerDto.lastModifiedDate( dateMapper.asOffsetDateTime( customer.getLastModifiedDate() ) );
        if ( customer.getVersion() != null ) {
            customerDto.version( customer.getVersion().intValue() );
        }

        return customerDto.build();
    }

    @Override
    public Customer dtoToCustomer(CustomerDto dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerBuilder customer = Customer.builder();

        customer.createdDate( dateMapper.asTimestamp( dto.getCreatedDate() ) );
        customer.id( dto.getId() );
        customer.lastModifiedDate( dateMapper.asTimestamp( dto.getLastModifiedDate() ) );
        if ( dto.getVersion() != null ) {
            customer.version( dto.getVersion().longValue() );
        }

        return customer.build();
    }
}
