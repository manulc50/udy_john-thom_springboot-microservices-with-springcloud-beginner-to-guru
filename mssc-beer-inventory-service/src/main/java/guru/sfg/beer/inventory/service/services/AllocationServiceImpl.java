package guru.sfg.beer.inventory.service.services;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {
	
	private final BeerInventoryRepository beerInventoryRepository;

	@Override
	public Boolean allocateOrder(BeerOrderDto beerOrderDto) {
		log.debug("Allocation OrderId: {}", beerOrderDto.getId());
		
		AtomicInteger totalOrdered = new AtomicInteger();
		AtomicInteger totalAllocated = new AtomicInteger();
		
		beerOrderDto.getBeerOrderLines().forEach(orderLine -> {
			
			allocateBeerOrderLine(orderLine);
			
			int orderQty = orderLine.getOrderQuantity() != null ? orderLine.getOrderQuantity() : 0;
			int allocatedQty = orderLine.getQuantityAllocated() != null ? orderLine.getQuantityAllocated() : 0;
			
			totalOrdered.set(totalOrdered.get() + orderQty);
			totalAllocated.set(totalAllocated.get() + allocatedQty);
			
		});
		
		log.debug("Total Ordered: {}, Total Allocated: {}", totalOrdered.get(), totalAllocated.get());
		
		return totalOrdered.get() == totalAllocated.get();
	}
	
	@Override
	public void deallocateBeerOrder(BeerOrderDto beerOrderDto) {
		beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
			BeerInventory beerInventory = BeerInventory.builder()
					.beerId(beerOrderLineDto.getBeerId())
					.upc(beerOrderLineDto.getUpc())
					.quantityOnHand(beerOrderLineDto.getQuantityAllocated())
					.build();
			
			BeerInventory savedInventory = beerInventoryRepository.save(beerInventory);
			
			log.debug("Saved Inventory for beer upc: {}, invenotry id: {}", beerOrderLineDto.getUpc(), savedInventory.getId());
		});
		
	}
	
	private void allocateBeerOrderLine(BeerOrderLineDto orderLine) {
		
		int orderQty = orderLine.getOrderQuantity() != null ? orderLine.getOrderQuantity() : 0;
		int allocatedQty = orderLine.getQuantityAllocated() != null ? orderLine.getQuantityAllocated() : 0;
		int qtyToAllocate = orderQty - allocatedQty;
		
		if(qtyToAllocate > 0) {
		
			List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByUpc(orderLine.getUpc());
	
			beerInventoryList.forEach(beerInventory -> {
				int inventory = beerInventory.getQuantityOnHand() == null ? 0 : beerInventory.getQuantityOnHand();
				
				// Full allocation
				if(inventory >= qtyToAllocate) {
					inventory = inventory - qtyToAllocate;
					orderLine.setQuantityAllocated(orderQty);
					beerInventory.setQuantityOnHand(inventory);
					
					beerInventoryRepository.save(beerInventory);
				}
				// Partial allocation
				else if (inventory > 0) { //partial allocation
	                orderLine.setQuantityAllocated(allocatedQty + inventory);
	                beerInventory.setQuantityOnHand(0);
	            }
	
	            if (beerInventory.getQuantityOnHand() == 0) {
	                beerInventoryRepository.delete(beerInventory);
	            }
	        });
		}
	}

}
