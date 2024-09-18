package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/*
 * The default HttpClient used in the RestTemplate is provided by the JDK.
 * It is developed on top of the HttpURLConnection.
 * There is a new module added in Java 9 in incubation status and standardized in Java 11 called java.net.http.HttpClient.
 * We can use this to make a client connection as well without needing third-party libraries. It is still unclear whether this will be used in Spring clients.
 * Let’s get back to the business.
 * In Spring, the default HTTP client can be changed to Apache’s HttpClient or Square’s OkHttpClient. We can configure the RestTemplate to use the HttpClient of our choice.
 * We can do this either directly or by using Spring Cloud Commons org.springframework.cloud.commons.httpclient which provides ApacheHttpClientFactory and OkHttpClientFactory.
 * Both are solid Http Client implementations used by many projects/libraries with a great community.
 */

// Clase para configurar RestTemplate para que use un cliente http bloqueante de Apache 


@Primary
@Component
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {
	
	private final Integer maxTotalConnections;
	private final Integer defaultMaxTotalConnections;
	private final Integer connectionRequestTimeout;
	private final Integer socketTimeout;

	
	public BlockingRestTemplateCustomizer(@Value("${sfg.maxtotalconnections}") Integer maxTotalConnections,
										  @Value("${sfg.defaultmaxtotalconnections}") Integer defaultMaxTotalConnections,
										  @Value("${sfg.connectionrequesttimeout}") Integer connectionRequestTimeout,
										  @Value("${sfg.sockettimeout}") Integer socketTimeout) {

		this.maxTotalConnections = maxTotalConnections;
		this.defaultMaxTotalConnections = defaultMaxTotalConnections;
		this.connectionRequestTimeout = connectionRequestTimeout;
		this.socketTimeout = socketTimeout;
	}

	// Método que configura la instancia RestTemplate para que use nuestra configuración del cliente Http bloqueante de Apache
	@Override
	public void customize(RestTemplate restTemplate) {
		restTemplate.setRequestFactory(clientHttpRequestFactory());
	}
	
	// Método con la configuración personalizada del cliente Http bloqueante de Apache
	private ClientHttpRequestFactory clientHttpRequestFactory(){
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(defaultMaxTotalConnections);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(requestConfig)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

}
