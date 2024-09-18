package guru.sfg.brewery.model.events;

import guru.sfg.brewery.model.BeerDto;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class NewInventoryEvent extends BeerEvent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3528200187614460181L;
	
	public NewInventoryEvent(BeerDto beerDto) {
		super(beerDto);
	}

}
