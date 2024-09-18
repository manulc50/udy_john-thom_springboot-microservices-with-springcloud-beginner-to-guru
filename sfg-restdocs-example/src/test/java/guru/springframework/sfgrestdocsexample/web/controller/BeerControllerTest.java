package guru.springframework.sfgrestdocsexample.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import guru.springframework.sfgrestdocsexample.domain.Beer;
import guru.springframework.sfgrestdocsexample.repositories.BeerRepository;
import guru.springframework.sfgrestdocsexample.web.controller.BeerController;
import guru.springframework.sfgrestdocsexample.web.model.BeerDto;
import guru.springframework.sfgrestdocsexample.web.model.BeerStyleEnum;

// Nota: Para realizar los tests y generar la documentación con Rest Docs, se debe usar las utilidades del paquete "org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders" en lugar del paquete "org.springframework.test.web.servlet.request.MockMvcRequestBuilders"(Este paquete se usa en tests sin Rest Docs)

@ExtendWith(RestDocumentationExtension.class)
// Por defecto, la uri de acceso a la documentación generada es "http://localhost/" `+ puerto, donde "puerto" es el número de puerto donde arranca esta aplicación Spring Boot.
// Podemos personalizar esta uri usando los atributos "uriScheme","uriHost" y "uriPort" de esta anotación
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "dev.springframework.guru", uriPort = 80) // Ahora la uri de acceso a la documentación es "https://dev.springframework.guru:80" en lugar de "http://localhost/" `+ puerto
@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "guru.springframework.sfgrestdocsexample.web.mappers")
public class BeerControllerTest {
	
	@MockBean
	private BeerRepository beerRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private BeerDto validBeer;
	
	@BeforeEach
	public void setUp() {
		validBeer = BeerDto.builder()
				.beerName("Nice Ale")
				.beerStyle(BeerStyleEnum.ALE)
				.price(new BigDecimal(9.99))
				.upc(123123123123L)
				.build();
	}
	
	@Test
	void getBeerByIdTest() throws Exception {
		given(beerRepository.findById(any())).willReturn(Optional.of(Beer.builder().id(UUID.randomUUID()).build()));
		
		mockMvc.perform(get("/api/v1/beer/{beerId}", UUID.randomUUID().toString())
				.param("isCold","yes") // Pasamos a la url de la petición http el parámetro "isCold" con valor "yes". El método handler del controlador asociado a esta url para esta petición http no maneja ningún parámetro de la url y, por lo tanto, este parámetro será ignorado por este método handler. Realmente, pasamos este parámetro a la url para mostrar un ejemplo sobre cómo documentar un parámetro que viaja en la url de una petición http
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("v1/beer-get",
						// Para documentar variables que viajan en la url de la petición http
						pathParameters(
								parameterWithName("beerId").description("UUID for desired beer to get")
						),
						// Para documentar parámetros que viajan en la url de la petición http
						requestParameters(
								parameterWithName("isCold").description("Is Beer Cold Query param")
						),
						// Para documentar las propiedades o campos de la entidad que se responde en la petición http
						// Se debe documentar todas las propiedades o campos de la respuesta de la petición http que realiza esta prueba, ya que, de no hacerlo, fallará
						responseFields(
								fieldWithPath("id").description("Id of Beer"),
	                            fieldWithPath("version").description("Version number"),
	                            fieldWithPath("createdDate").description("Date Created"),
	                            fieldWithPath("lastModifiedDate").description("Date Updated"),
	                            fieldWithPath("beerName").description("Beer Name"),
	                            fieldWithPath("beerStyle").description("Beer Style"),
	                            fieldWithPath("upc").description("UPC of Beer"),
	                            fieldWithPath("price").description("Price"),
	                            fieldWithPath("quantityOnHand").description("Quantity On hand")
						)));
	}
	
	@Test
	void saveNewBeerTest() throws Exception {
		BeerDto beerDto = validBeer;
		String beerDtoJson = objectMapper.writeValueAsString(beerDto);
		
		// Creamos una instancia de la clase "ConstrainedFields" para poder documentar correctamente las validaciones de las propiedades o campos de la entidad "BeerDto" que se envía en la petición http de esta prueba
		ConstrainedFields fields = new ConstrainedFields(BeerDto.class);
		
		given(beerRepository.save(any())).willReturn(Beer.builder().id(UUID.randomUUID()).build());
			
		mockMvc.perform(post("/api/v1/beer/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(beerDtoJson))
				.andExpect(status().isCreated())
				.andDo(document("v1/beer-new",
						// Para documentar las propiedades o campos de la entidad que se envía en la petición http
						// Se debe documentar todas las propiedades o campos de la petición http que realiza esta prueba, ya que, de no hacerlo, fallará
						// Las propiedades que usan el método "ignored()" son ignoradas en esta prueba para que no den error debido a que no están documentadas.
						// Y no las documentamos porque, aunque son propiedades de la entidad que en envía en la petición http que se realiza en esta prueba, sus valores no son introducidos por el cliente que realiza este tipo de peticiones http porque son generados dentro de la aplicación para posteriormente enviarse en las respuestas de otras peticiones http 
						requestFields(
								// Sustituimos el método "fieldWithPath" por el método "withPath" de la instancia "fields" para que se documenten las validaciones de las propiedades o campos de la entidad que se envía en la petición http de esta prueba
								// Si la entidad no tuviera validaciones de propiedades o campos, entonces podríamos seguir usando el método "fieldWithPath" en cada una de esas propiedades o campos
								fields.withPath("id").ignored(),
								fields.withPath("version").ignored(),
								fields.withPath("createdDate").ignored(),
								fields.withPath("lastModifiedDate").ignored(),
								fields.withPath("beerName").description("Name of the beer"),
								fields.withPath("beerStyle").description("Style of Beer"),
								fields.withPath("upc").description("Beer UPC").attributes(),
								fields.withPath("price").description("Beer Price"),
								fields.withPath("quantityOnHand").ignored()
						)));
	}
	
	@Test
	void updateBeerByIdTest() throws Exception {
		BeerDto beerDto = validBeer;
		String beerDtoJson = objectMapper.writeValueAsString(beerDto);
		
		mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(beerDtoJson))
				.andExpect(status().isNoContent());
	}
	
	// Esta clase es necesaria para poder documentar las validaciones de las propiedades o campos de las entidades que se envían en algunas peticiones http
	private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }

}
