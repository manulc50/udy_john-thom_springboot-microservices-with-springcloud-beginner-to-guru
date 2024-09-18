package guru.springframework.msscbrewery.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.msscbrewery.services.BeerService;
import guru.springframework.msscbrewery.web.model.BeerDto;

@Deprecated
@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {
	
	@Autowired
	private BeerService beerService;
	
	@GetMapping("/{beerId}")
	public ResponseEntity<BeerDto> getBeer(@PathVariable(value="beerId") UUID id){
		return new ResponseEntity<BeerDto>(beerService.getBeerById(id),HttpStatus.OK);
	}
	
	// Se puede usar la anotación @Valid de JSR-303 o la anotación @validated de Spring para realizar las validaciones. Aquí usamos la anotación @Valid de JSR-303
	@PostMapping
	public ResponseEntity<BeerDto> createBeer(@Valid @RequestBody BeerDto beerDto){
		BeerDto beerStored = beerService.saveNewBeer(beerDto);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location","/api/v1/beer/" + beerStored.getId());
		return new ResponseEntity<BeerDto>(beerStored,headers,HttpStatus.CREATED);
	}
	
	// Se puede usar la anotación @Valid de JSR-303 o la anotación @validated de Spring para realizar las validaciones. Aquí usamos la anotación @Valid de JSR-303
	@PutMapping("/{beerId}")
	public ResponseEntity<Void> updateBeer(@PathVariable UUID beerId,@Valid @RequestBody BeerDto beerDto) {
		beerService.updateBeer(beerId, beerDto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{beerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBeer(@PathVariable UUID beerId) {
		beerService.deleteById(beerId);
	}

}
