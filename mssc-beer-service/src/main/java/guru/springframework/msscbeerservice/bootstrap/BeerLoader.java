package guru.springframework.msscbeerservice.bootstrap;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.sfg.brewery.model.BeerStyleEnum;

@Component
public class BeerLoader implements CommandLineRunner{

	public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";
	
	@Autowired
	private BeerRepository beerRepository;

	@Override
	public void run(String... args) throws Exception {
		loadBeerObjects();
		
	}
	
	private void loadBeerObjects(){
		if(beerRepository.count() == 0) {
			beerRepository.save(Beer.builder()
					.beerName("Mango Bobs")
					.beerStyle(BeerStyleEnum.IPA.name())
					.quantityToBrew(200)
					.minOnHand(12)
					.upc(BEER_1_UPC)
					.price(new BigDecimal(12.95))
					.build());
			
			beerRepository.save(Beer.builder()
					.beerName("Galaxy Cat")
					.beerStyle(BeerStyleEnum.PALE_ALE.name())
					.quantityToBrew(200)
					.minOnHand(12)
					.upc(BEER_2_UPC)
					.price(new BigDecimal(12.95))
					.build());
			
			beerRepository.save(Beer.builder()
					.beerName("Pinball Porter")
					.beerStyle(BeerStyleEnum.PALE_ALE.name())
					.quantityToBrew(200)
					.minOnHand(12)
					.upc(BEER_3_UPC)
					.price(new BigDecimal(12.95))
					.build());
		}
		
		
	}

}
