package guru.springframework.msscssm.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
	
	@Id
	@GeneratedValue // La estrategia por defecto a usar es AUTO
	private Long id;
	
	@Enumerated(EnumType.STRING) // Para que se guarde en la base de datos el nombre de la enumeración en lugar del número(opción por defecto)
	private PaymentState state;
	
	private BigDecimal amount;

}
