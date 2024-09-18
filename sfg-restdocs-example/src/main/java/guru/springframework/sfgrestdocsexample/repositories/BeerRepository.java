package guru.springframework.sfgrestdocsexample.repositories;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

import guru.springframework.sfgrestdocsexample.domain.Beer;

public interface BeerRepository extends PagingAndSortingRepository<Beer,UUID>{

}
