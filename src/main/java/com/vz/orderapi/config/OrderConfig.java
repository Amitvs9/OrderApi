package com.vz.orderapi.config;

import com.vz.orderapi.util.Constant;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * OrderConfig
 * All configs which are relevant for application.
 */
@Configuration
@EnableAsync
public class OrderConfig {

    private final Integer connectTimeout;
    private final Integer readTimeout;

    private final String moduleName;
    private final String apiVersion;

    public OrderConfig(@Value("${order.api.connectTimeout}") Integer connectTimeout,
                       @Value("${order.api.readTimeout}") Integer readTimeout,
                       @Value("${module-name}") String moduleName,
                       @Value("${api-version}") String apiVersion) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.moduleName = moduleName;
        this.apiVersion = apiVersion;
    }

    /**
     * Create instance of restTemplate to call Api
     *
     * @return RestTemplate
     */
    @Bean("apiRestTemplate")
    public RestTemplate apiRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        restTemplate.setRequestFactory(requestFactory);
        final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            final HttpHeaders headers = request.getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add(Constant.USER_AGENT, Constant.USER_AGENT_VALUE);
            return execution.execute(request, body);
        });
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    /**
     * Thread Executor pool
     *
     * @return Executor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("OrderThread");
        executor.initialize();
        return executor;
    }

    /**
     * open-api configuration about application name and version
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String apiTitle = String.format("%s", StringUtils.capitalize(moduleName));
        return new OpenAPI()
                .info(new Info().title(apiTitle).version(apiVersion));
    }
}
