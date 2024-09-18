package guru.springframework.msscbeerservice.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Beer {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Type(type = "org.hibernate.type.UUIDCharType") // Para evitar el error de ejecución 'Hexadecimal string contains non-hex character: "0a818933-087d-47f2-ad83-2f986ed087eb"'
	@Column(length = 36, columnDefinition ="varchar(36)", updatable = false, nullable = false)
	private UUID id;
	private String beerName;
	private String beerStyle;
	private BigDecimal price;
	private Integer minOnHand;
	private Integer quantityToBrew;
	
	@Version
	private Long version;
	
	@Column(unique = true)
	private String upc;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp createdDate;
	
	@UpdateTimestamp
	private Timestamp lastModifiedDate;

}
