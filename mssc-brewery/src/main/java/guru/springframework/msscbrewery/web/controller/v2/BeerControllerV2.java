package guru.springframework.msscbrewery.web.controller.v2;

import java.util.UUID;

import javax.validation.Valid;

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

import guru.springframework.msscbrewery.services.V2.BeerServiceV2;
import guru.springframework.msscbrewery.web.model.v2.BeerDtoV2;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/beer")
public class BeerControllerV2 {
	
	private final BeerServiceV2 beerServiceV2;
	
	@GetMapping("/{beerId}")
	public ResponseEntity<BeerDtoV2> getBeer(@PathVariable(value="beerId") UUID id){
		return new ResponseEntity<BeerDtoV2>(beerServiceV2.getBeerById(id),HttpStatus.OK);
	}
	
	// Se puede usar la anotación @Valid de JSR-303 o la anotación @validated de Spring para realizar las validaciones. Aquí usamos la anotación @Valid de JSR-303
	@PostMapping
	public ResponseEntity<BeerDtoV2> createBeer(@Valid @RequestBody BeerDtoV2 beerDto){
		log.debug("in handle post...");
		val beerStored = beerServiceV2.saveNewBeer(beerDto);
		var headers = new HttpHeaders();
		headers.add("Location","/api/v1/beer/" + beerStored.getId());
		return new ResponseEntity<BeerDtoV2>(beerStored,headers,HttpStatus.CREATED);
	}
	
	// Se puede usar la anotación @Valid de JSR-303 o la anotación @validated de Spring para realizar las validaciones. Aquí usamos la anotación @Valid de JSR-303
	@PutMapping("/{beerId}")
	public ResponseEntity<Void> updateBeer(@PathVariable UUID beerId,@Valid @RequestBody BeerDtoV2 beerDto) {
		beerServiceV2.updateBeer(beerId, beerDto);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/{beerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBeer(@PathVariable UUID beerId) {
		beerServiceV2.deleteById(beerId);
	}

}
