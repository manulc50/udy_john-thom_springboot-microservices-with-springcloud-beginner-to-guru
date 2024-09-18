package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
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

// Clase para configurar RestTemplate para que use un cliente http no bloqueante de Apache 


@Component
public class NIORestTemplateCustomizer implements RestTemplateCustomizer {
	
	private final Integer connectTimeout;
	private final Integer ioThreadCount;
	private final Integer soTimeout;
	private final Integer defaultMaxPerRoute;
	private final Integer maxTotal;
	
	public NIORestTemplateCustomizer(@Value("${sfg.connecttimeout}") Integer connectTimeout,
									 @Value("${sfg.iothreadcount}")Integer ioThreadCount,
									 @Value("${sfg.sotimeout}") Integer soTimeout,
									 @Value("${sfg.defaultmaxperroute}") Integer defaultMaxPerRoute,
									 @Value("${sfg.maxtotal}") Integer maxTotal) {

		this.connectTimeout = connectTimeout;
		this.ioThreadCount = ioThreadCount;
		this.soTimeout = soTimeout;
		this.defaultMaxPerRoute = defaultMaxPerRoute;
		this.maxTotal = maxTotal;
	}

	// Método que configura la instancia RestTemplate para que use nuestra configuración del cliente Http no bloqueante de Apache
	@Override
	public void customize(RestTemplate restTemplate) {
		try {
            restTemplate.setRequestFactory(clientHttpRequestFactory());
        }
		catch (IOReactorException e) {
            e.printStackTrace();
        }
	}
	
	// Método con la configuración personalizada del cliente Http no bloqueante de Apache
	private ClientHttpRequestFactory clientHttpRequestFactory() throws IOReactorException {
        final DefaultConnectingIOReactor ioreactor = new DefaultConnectingIOReactor(IOReactorConfig.custom().
                setConnectTimeout(connectTimeout).
                setIoThreadCount(ioThreadCount).
                setSoTimeout(soTimeout).
                build());

        final PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioreactor);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connectionManager.setMaxTotal(maxTotal);

        CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        return new HttpComponentsAsyncClientHttpRequestFactory(httpAsyncClient);

    }

}
