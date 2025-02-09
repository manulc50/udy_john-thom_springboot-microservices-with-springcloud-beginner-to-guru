package guru.springframework.sfgrestdocsexample.web.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
	
	@Null
	private UUID id;
	
	@Null
	private Integer version;
	
	@NotBlank
	@Size(min = 3, max = 100)
	private String beerName;
	
	@NotNull
	private BeerStyleEnum beerStyle;
	
	@Null
	private OffsetDateTime createdDate;
	
	@Null
	private OffsetDateTime lastModifiedDate;
	
	@NotNull
	@Positive
	private Long upc;
	
	@NotNull
	@Positive
	private BigDecimal price;
	
	@Positive
	private Integer quantityOnHand;
}
