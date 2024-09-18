package guru.sfg.beer.order.service.services.beer;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import guru.sfg.brewery.model.BeerDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
@Component
public class BeerServiceRestTemplateImpl implements BeerService{
	
	public static final String BEER_PATH_V1 = "/api/v1/beer/{beerUpc}/beerUpc";
	private RestTemplate restTemplate;
	private String beerServiceHost;
	
	public BeerServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	public void setBeerServiceHost(String beerServiceHost) {
		this.beerServiceHost = beerServiceHost;
	}

	@Override
	public Optional<BeerDto> getBeerByUpc(String upc) {
		log.debug("Calling Beer Service");
		
		return Optional.of(restTemplate.getForObject(beerServiceHost + BEER_PATH_V1,BeerDto.class,upc));
	}

}
