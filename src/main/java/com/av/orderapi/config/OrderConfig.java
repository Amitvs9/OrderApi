package com.av.orderapi.config;

import com.av.orderapi.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * OrderConfig
 * All configs which are relevant for application.
 */
@RequiredArgsConstructor
@Component
public class OrderConfig {

    @Value("${order.api.connectTimeout}")
    private Integer connectTimeout;
    @Value("${order.api.readTimeout}")
    private Integer readTimeout;

    /**
     * Create instance of restTemplate to call Api
     *
     * @return RestTemplate
     */
    @Bean("apiRestTemplate")
    public RestTemplate apiRestTemplate( ) {
        final RestTemplate restTemplate = new RestTemplate();
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        restTemplate.setRequestFactory( requestFactory );
        final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add( (request, body, execution) -> {
            final HttpHeaders headers = request.getHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON );
            headers.setAccept( Collections.singletonList( MediaType.APPLICATION_JSON ) );
            headers.add( Constant.USER_AGENT, Constant.USER_AGENT_VALUE );
            return execution.execute( request, body );
        } );
        restTemplate.setInterceptors( interceptors );
        return restTemplate;
    }

    /**
     * Thread Executor pool
     *
     * @return Executor
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor( ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("asynch-OrderTask-");
        executor.initialize();
        return executor;
    }
}
