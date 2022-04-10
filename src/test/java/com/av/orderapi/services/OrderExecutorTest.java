package com.av.orderapi.services;

import com.av.orderapi.dto.Data;
import com.av.orderapi.dto.OrderDTO;
import com.av.orderapi.entities.OrderEntity;
import com.av.orderapi.errors.ErrorCodes;
import com.av.orderapi.errors.OrderException;
import com.av.orderapi.repos.OrderRepository;
import com.av.orderapi.webserviceClient.CustomerApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderExecutorTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    OrderExecutor orderExecutor;

    @Mock
    CustomerApiClient customerApiClient;

    private OrderEntity orderEntity;

    @BeforeEach
    void init( ) {
        orderEntity = OrderEntity
                .builder()
                .orderId( 10L )
                .email( "amitv@mail.com" )
                .firstName( "Amit" )
                .lastName( "Vashishtha" )
                .productId( 1234L )
                .build();
    }

    @Test
    public void test_GetAll_Orders( ) {
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(orderEntity);
        Page<OrderEntity> pagedTasks = new PageImpl<>(orderEntityList);
        when(orderRepository.findAll(PageRequest.of(0,1))).thenReturn(pagedTasks);
        List<OrderEntity> orderEntities = orderExecutor.getAllOrders( 0, 1 );
        assertEquals( 10L, orderEntities.get( 0 ).getOrderId() );
        assertEquals( "amitv@mail.com", orderEntities.get(0).getEmail());
        assertEquals( "Amit", orderEntities.get(0).getFirstName() );
        assertEquals( "Vashishtha", orderEntities.get(0).getLastName() );
        assertEquals( 1234L, orderEntities.get( 0 ).getProductId() );

    }

    @Test
    public void test_No_Order_Available_Exception( ) {
        List<OrderEntity> orderEntityList = new ArrayList<>();
        Page<OrderEntity> pagedTasks = new PageImpl<>(orderEntityList);
        when(orderRepository.findAll(PageRequest.of(0,1))).thenReturn(pagedTasks);
        assertThrows(OrderException.class, ()-> orderExecutor.getAllOrders( 0, 1 ));

    }

    @Test
    public void test_CreateOrderWithEmailIdAndProductId( ) {
        when(orderRepository.save(any())).thenReturn( orderEntity );
        when(customerApiClient.retrieveCustomerDetails(anyString())).thenReturn(forData());
        assertEquals( 10L, orderExecutor.createOrder(formOrderDto()) );
    }

    @Test
    public void test_CreateOrderWithInvalidEmailId( ) {
        when(customerApiClient.retrieveCustomerDetails(anyString())).thenThrow(new OrderException( ErrorCodes.NOT_FOUND,"emailId not found"));
        assertThrows(OrderException.class, ()->orderExecutor.createOrder(formOrderDto()));
    }

    @Test
    public void test_CreateOrderWithInvalidProductId( ) {
        when(customerApiClient.retrieveCustomerDetails(anyString())).thenReturn(forData());
        when(orderRepository.findByProductId(Mockito.anyLong() )).thenReturn(Optional.of(orderEntity));
        assertThrows(OrderException.class, ()->orderExecutor.createOrder(formOrderDto()));
    }

    private CompletableFuture<Data> formCustomerDetails( ) {
        Data data = forData();
        return CompletableFuture.completedFuture(data);
    }

    private Data forData( ) {
        return Data.builder()
                .email( "amitv@mail.com" )
                .firstName( "Amit" )
                .lastName( "Vs" )
                .build();
    }

    private CompletableFuture<Long> formProductId( ) {
        return CompletableFuture.completedFuture(10L);
    }

    private OrderDTO formOrderDto( ) {
        return OrderDTO.builder()
                .email( "amitv@mail.com" )
                .productId( 1234L ).build();
    }
}
