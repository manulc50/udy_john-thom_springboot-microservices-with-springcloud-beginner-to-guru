package guru.springframework.msscbeerservice.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import guru.springframework.msscbeerservice.domain.Beer;
import guru.springframework.msscbeerservice.repositories.BeerRepository;
import guru.springframework.msscbeerservice.web.controller.NotFoundException;
import guru.springframework.msscbeerservice.web.mappers.BeerMapper;
import guru.sfg.brewery.model.BeerDto;
import guru.sfg.brewery.model.BeerPagedList;
import guru.sfg.brewery.model.BeerStyleEnum;

@Service
public class BeerServiceImpl implements BeerService{
	
	@Autowired
	private BeerRepository beerRepository ;
	
	@Autowired
	private BeerMapper beerMapper;
	
	@Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false")
	@Transactional(readOnly = true)
	@Override
	public BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, Boolean showInventoryOnHand, PageRequest pageRequest) {

		Page<Beer> beerPage;
		
		if(!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle))
			beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName,beerStyle,pageRequest);
		else if(!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle))
			beerPage = beerRepository.findAllByBeerName(beerName,pageRequest);
		else if(StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle))
			beerPage = beerRepository.findAllByBeerStyle(beerStyle,pageRequest);
		else
			beerPage = beerRepository.findAll(pageRequest);
		
		List<BeerDto> listBeerDtos = null;
		if(showInventoryOnHand)
			listBeerDtos = beerPage.getContent().stream()
					.map(beerMapper::beerToBeerDtoWithInventory)
					.collect(Collectors.toList());
		else
			listBeerDtos = beerPage.getContent().stream()
			.map(beerMapper::beerToBeerDto)
			.collect(Collectors.toList());
		
		BeerPagedList beerPagedList = new BeerPagedList(listBeerDtos,PageRequest.of(beerPage.getPageable().getPageNumber(),beerPage.getPageable().getPageSize()),beerPage.getTotalElements());
		
		return beerPagedList;
	}

	@Cacheable(cacheNames = "beerCache", key = "#beerId", condition = "#showInventoryOnHand == false")
	@Transactional(readOnly = true)
	@Override
	public BeerDto getBeerById(UUID beerId, Boolean showInventoryOnHand) {
		return showInventoryOnHand ? beerMapper.beerToBeerDtoWithInventory(beerRepository.findById(beerId).orElseThrow(() -> new NotFoundException()))
				: beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElseThrow(() -> new NotFoundException()));
	}
	
	@Cacheable(cacheNames = "beerUpcCache", key = "#beerUpc", condition = "#showInventoryOnHand == false")
	@Transactional(readOnly = true)
	@Override
	public BeerDto getBeerByUpc(String beerUpc, Boolean showInventoryOnHand) {
		System.out.println("It was called");
		return showInventoryOnHand ? beerMapper.beerToBeerDtoWithInventory(beerRepository.findByUpc(beerUpc).orElseThrow(() -> new NotFoundException()))
				: beerMapper.beerToBeerDto(beerRepository.findByUpc(beerUpc).orElseThrow(() -> new NotFoundException()));
		
	}

	@Transactional
	@Override
	public BeerDto saveNewBeer(BeerDto beerDto) {
		Beer beer = beerMapper.beerDtoToBeer(beerDto);
		return beerMapper.beerToBeerDto(beerRepository.save(beer));
	}

	@Transactional
	@Override
	public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {
		Beer beer = beerRepository.findById(beerId).orElseThrow(() -> new NotFoundException());
		beer.setBeerName(beerDto.getBeerName());
		beer.setBeerStyle(beerDto.getBeerStyle().name());
		beer.setPrice(beerDto.getPrice());
		beer.setUpc(beer.getUpc());
		
		return beerMapper.beerToBeerDto(beerRepository.save(beer));
		
	}

	@Transactional
	@Override
	public void deleteBeerById(UUID beerId) {
		beerRepository.deleteById(beerId);
	}

}
