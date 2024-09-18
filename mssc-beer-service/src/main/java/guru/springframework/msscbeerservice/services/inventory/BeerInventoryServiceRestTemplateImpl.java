package guru.springframework.msscbeerservice.services.inventory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import guru.sfg.brewery.model.BeerInventoryDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("!local-discovery") // Esta configuración sólo se va a tener en cuenta cuando esta aplicación Spring Boot no se ejecute con el perfil "local-discovery". De esta manera, nos aseguramos de que sólo haya una implementación de la interfaz "BeerInventoryService" cada vez que se ejecute la aplicación 
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = true)
@Component
public class BeerInventoryServiceRestTemplateImpl implements BeerInventoryService{
	
	public final static String INVENTORY_PATH = "/api/v1/beer/{beerId}/inventory";
	private final RestTemplate restTemplate;
	private String beerInventoryServiceHost;
	
	// Nota: Como los valores de las propiedades "inventoryUser" y "inventoryPassword", al igual que el valor de la propiedad "beerInventoryServiceHost", están externalizados en el archivo "application.properties" pero se tienen que usar en el constructor de esta clase para poder pasar esos valores al contexto de seguridad del bean RestTemplate, no podemos inyectar esos valores en esas propiedades usando la anotación "@ConfigurationProperties" porque no se inyectan en el momento de construirse esta clase y serían propiedades nulas
	// Por esta razón, inyectamos los valores de las propiedades "inventoryUser" y "inventoryPassword" con la anotación @Value en el constructor de esta clase
	// También, por esta razón, tenemos que poner el atributo "ignoreUnknownFields = true" de la anotación @ConfigurationProperties porque si lo dejamos en "false", y como además las propiedades "inventoryUser" y "inventoryPassword" también tienen sus valores externalizados en el archivo "application.properties" usando el prefijo "sfg.brewery", Spring Boot va a intentar localizar los métodos setter de esas propiedades para poder inyectar sus valores y, como no están, provocará un fallo de ejecución de la aplicación. Por eso, tenemos que poner el atributo "ignoreUnknownFields = true" para que Spring Boot ignore las propiedades con prefijo "sfg.brewery" que no tienen métodos setters implementados en esta clase y así evitar un error de ejecución en esta aplicación  
	
	public BeerInventoryServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder,
			@Value("${sfg.brewery.inventory-user}") String inventoryUser, @Value("${sfg.brewery.inventory-password}") String inventoryPassword) {
		
		this.restTemplate = restTemplateBuilder
				.basicAuthentication(inventoryUser,inventoryPassword) // Configuramos las credenciales requeridas por el microservicio "mssc-beer-inventory-service", que es el microservicio con el que vamos a comunicarnos
				.build();
	}
	
	// Este método setter para la propiedad "beerInventoryServiceHost" es necesario para la anotación @ConfigurationProperties para que puede realizar la inyección de su valor, que se encuentra externalizado en el archivo "application.properties, una vez que se haya construido esta clase y sea reclamada dicha propiedad en alguna parte del código
	public void setBeerInventoryServiceHost(String beerInventoryServiceHost) {
		this.beerInventoryServiceHost = beerInventoryServiceHost;
	}

	@Override
	public Integer getOnHandInventory(UUID beerId) {
		log.debug("Calling Inventory Service");
		
		ResponseEntity<List<BeerInventoryDto>> responseEntity = restTemplate.exchange(beerInventoryServiceHost + INVENTORY_PATH,HttpMethod.GET,null,new ParameterizedTypeReference<List<BeerInventoryDto>>(){},beerId);
		
		Integer onHand = Objects.requireNonNull(responseEntity.getBody())
				.stream()
				.mapToInt(beerInventoryDto -> beerInventoryDto.getQuantityOnHand())
				.sum();

		return onHand;
	}

}
