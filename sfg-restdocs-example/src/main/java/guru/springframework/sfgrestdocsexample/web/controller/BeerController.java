package guru.springframework.sfgrestdocsexample.web.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import guru.springframework.sfgrestdocsexample.domain.Beer;
import guru.springframework.sfgrestdocsexample.repositories.BeerRepository;
import guru.springframework.sfgrestdocsexample.web.mappers.BeerMapper;
import guru.springframework.sfgrestdocsexample.web.model.BeerDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/beer")
public class BeerController {
	
	private final BeerMapper beerMapper;
	private final BeerRepository beerRepository;
	
	@GetMapping("/{beerId}")
	public ResponseEntity<BeerDto> getBeerById(@PathVariable UUID beerId){
		Beer beer = beerRepository.findById(beerId).get();
		return new ResponseEntity<BeerDto>(beerMapper.beerToBeerDto(beer),HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<BeerDto> saveNewBeer(@Validated @RequestBody BeerDto beerDto) {
		Beer savedBeer = beerRepository.save(beerMapper.beerDtoToBeer(beerDto));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location","/api/v1/beer/" + savedBeer.getId().toString());
		return new ResponseEntity<BeerDto>(beerMapper.beerToBeerDto(savedBeer),headers,HttpStatus.CREATED);
	}
	
	@PutMapping("/{beerId}")
	public ResponseEntity<Void> updateBeerById(@PathVariable UUID beerId,@Validated @RequestBody BeerDto beerDto){
		beerRepository.findById(beerId).ifPresent(beer -> {
			beer.setBeerName(beerDto.getBeerName());
			beer.setBeerStyle(beerDto.getBeerStyle().name());
			beer.setPrice(beerDto.getPrice());
			beer.setUpc(beerDto.getUpc());
			
			beerRepository.save(beer);
		});
		
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
