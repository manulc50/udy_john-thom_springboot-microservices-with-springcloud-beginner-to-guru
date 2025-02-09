package guru.sfg.brewery.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9055568535713635288L;

	@Null
	private UUID id;
	
	@Null
	private Integer version;
	
	@NotBlank
	private String beerName;
	
	@NotBlank
	private String beerStyle;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
	@Null
	private OffsetDateTime createdDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
	@Null
	private OffsetDateTime lastModifiedDate;
	
	@NotNull
	private String upc;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@NotNull
	@Positive
	private BigDecimal price;
	
	private Integer quantityOnHand;
}
