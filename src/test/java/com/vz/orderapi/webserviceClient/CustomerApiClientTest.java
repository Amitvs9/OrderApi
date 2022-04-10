package com.vz.orderapi.webserviceClient;

import com.vz.orderapi.dto.Response;
import com.vz.orderapi.dto.Data;
import com.vz.orderapi.errors.OrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerApiClientTest {

    @InjectMocks
    private CustomerApiClient customerApiClient;

    @Mock
    private RestTemplate restTemplate;


    @BeforeEach
    public void setUp(){
        ReflectionTestUtils.setField(customerApiClient, "endpoint", "https://testapi.com");
        ReflectionTestUtils.setField(customerApiClient, "page", 1);
        ReflectionTestUtils.setField(customerApiClient, "perPage", 5);
    }

    @Test
    public void test_RetrieveCustomerDetailsFromAPI(){
        final Response apiResponse= Response.builder()
                .data( Collections.singletonList(forData())).build();
        ResponseEntity<Response> responseEntity = mock(ResponseEntity.class);
        Mockito.when(restTemplate.getForEntity( eq("https://testapi.com") ,eq( Response.class)
                ,Mockito.argThat(new ArgumentMatcher<Map<String, String>>() {
                    @Override
                    public boolean matches(Map<String, String> params) {
                        return true;

                    }
                } ) )).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(apiResponse);
        Data customer = customerApiClient.retrieveCustomerDetails("amitv@mail.com");
        assertEquals(customer.getEmail(), forData().getEmail());
    }

    @Test
    public void test_RetrieveCustomerDetailsFromAPI_EMAIL_NOT_FOUND(){
        final Response apiResponse= Response.builder()
                .data( Collections.singletonList(forData())).build();
        ResponseEntity<Response> responseEntity = mock(ResponseEntity.class);
        Mockito.when(restTemplate.getForEntity( eq("https://testapi.com") ,eq( Response.class)
                ,Mockito.argThat(new ArgumentMatcher<Map<String, String>>() {
                    @Override
                    public boolean matches(Map<String, String> params) {
                        return true;

                    }
                } ) )).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(apiResponse);
        assertThrows( OrderException.class,()-> customerApiClient.retrieveCustomerDetails("amit@mail.com"));
    }

    @Test
    public void test_RetrieveCustomerDetailsFromAPI_Exception(){
        Mockito.when(restTemplate.getForEntity( eq("https://testapi.com") ,eq( Response.class)
                ,Mockito.argThat(new ArgumentMatcher<Map<String, String>>() {
                    @Override
                    public boolean matches(Map<String, String> params) {
                        return true;

                    }
                } ) )).thenThrow(RestClientException.class);
        assertThrows(OrderException.class,()-> customerApiClient.retrieveCustomerDetails("amitv@mail.com"));
    }

    private Data forData( ) {
        return Data.builder()
                .email( "amitv@mail.com" )
                .firstName( "Amit" )
                .lastName( "Vs" )
                .build();
    }
}
