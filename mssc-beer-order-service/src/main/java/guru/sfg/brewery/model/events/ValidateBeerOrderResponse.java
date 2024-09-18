package guru.sfg.brewery.model.events;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateBeerOrderResponse {

	private UUID beerOrderId;
	private Boolean isValid;
}
