package guru.springframework.msscbeerservice.web.controller;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.msscbeerservice.services.BeerService;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerPagedList;
import guru.sfg.brewery.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {
	
	private static final Integer DEFAULT_PAGE_NUMBER = 0;
	private static final Integer DEFAULT_PAGE_SIZE = 25;
	
	private final BeerService beerService;
	
	@GetMapping
	public ResponseEntity<BeerPagedList> listBeers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
												   @RequestParam(value = "pageSize", required = false) Integer pageSize,
												   @RequestParam(value = "beerName", required = false) String beerName,
												   @RequestParam(value = "beerStyle", required = false) BeerStyleEnum beerStyle,
												   @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
	
		if(pageNumber == null || pageNumber < 0)
			pageNumber = DEFAULT_PAGE_NUMBER;
		
		if(pageSize == null || pageSize < 1)
			pageSize = DEFAULT_PAGE_SIZE;
		
		if(showInventoryOnHand == null)
			showInventoryOnHand = false;
		
		BeerPagedList beerList = beerService.listBeers(beerName,beerStyle,showInventoryOnHand,PageRequest.of(pageNumber,pageSize));
		
		return new ResponseEntity<BeerPagedList>(beerList,HttpStatus.OK);
	}
	
	@GetMapping("/{beerId}")
	public ResponseEntity<BeerDto> getBeerById(@PathVariable UUID beerId,@RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
		
		if(showInventoryOnHand == null)
			showInventoryOnHand = false;
		
		return new ResponseEntity<BeerDto>(beerService.getBeerById(beerId,showInventoryOnHand),HttpStatus.OK);
	}
	
	@GetMapping("/{upc}/beerUpc")
	public ResponseEntity<BeerDto> getBeerByUpc(@PathVariable(value = "upc") String beerUpc,@RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
		if(showInventoryOnHand == null)
			showInventoryOnHand = false;
		
		return ResponseEntity.ok().body(beerService.getBeerByUpc(beerUpc,showInventoryOnHand));
	}

	// Se puede usar la anotación @Valid de JSR-303 o la anotación @validated de Spring para realizar las validaciones. Aquí usamos la anotación @Validated de Spring
	@PostMapping
	public ResponseEntity<BeerDto> saveNewBeer(@Validated @RequestBody BeerDto beerDto) {
		BeerDto savedDto = beerService.saveNewBeer(beerDto);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location","/api/v1/beer/" + savedDto.getId().toString());
		return new ResponseEntity<BeerDto>(beerDto,headers,HttpStatus.CREATED);
	}
	
	// Se puede usar la anotación @Valid de JSR-303 o la anotación @validated de Spring para realizar las validaciones. Aquí usamos la anotación @Validated de Spring
	@PutMapping("/{beerId}")
	public ResponseEntity<Void> updateBeerById(@PathVariable UUID beerId,@Validated @RequestBody BeerDto beerDto){
		beerService.updateBeer(beerId, beerDto);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping("/{beerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteBeer(@PathVariable UUID beerId) {
		beerService.deleteBeerById(beerId);
	}
}
