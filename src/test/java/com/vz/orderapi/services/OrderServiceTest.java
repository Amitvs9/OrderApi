package com.vz.orderapi.services;

import com.vz.orderapi.dto.Customer;
import com.vz.orderapi.dto.OrderDTO;
import com.vz.orderapi.entities.OrderEntity;
import com.vz.orderapi.errors.ErrorCodes;
import com.vz.orderapi.errors.OrderException;
import com.vz.orderapi.repos.OrderRepository;
import com.vz.orderapi.webserviceClient.CustomerApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {


    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Mock
    private CustomerApiClient customerApiClient;

    @Mock
    private Executor taskExecutor;

    private OrderEntity forOrderEntity() {
        return OrderEntity
                .builder()
                .orderId(10L)
                .email("amitv@mail.com")
                .firstName("Amit")
                .lastName("Vashishtha")
                .productId(1234L)
                .build();
    }

    @Test
    public void test_GetAll_Orders() {
        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(forOrderEntity());
        Page<OrderEntity> pagedTasks = new PageImpl<>(orderEntityList);
        when(orderRepository.findAll(PageRequest.of(0, 1))).thenReturn(pagedTasks);
        List<OrderEntity> orderEntities = orderService.getAllOrders(0, 1);
        assertEquals(10L, orderEntities.get(0).getOrderId());
        assertEquals("amitv@mail.com", orderEntities.get(0).getEmail());
        assertEquals("Amit", orderEntities.get(0).getFirstName());
        assertEquals("Vashishtha", orderEntities.get(0).getLastName());
        assertEquals(1234L, orderEntities.get(0).getProductId());

    }

    @Test
    public void test_No_Order_Available_Exception() {
        List<OrderEntity> orderEntityList = new ArrayList<>();
        Page<OrderEntity> pagedTasks = new PageImpl<>(orderEntityList);
        when(orderRepository.findAll(PageRequest.of(0, 1))).thenReturn(pagedTasks);
        assertThrows(OrderException.class, () -> orderService.getAllOrders(0, 1));

    }


    @Test
    public void testCreateOrderWithProductIdAndEmailId() throws ExecutionException, InterruptedException {
        doAnswer(
                (InvocationOnMock invocation) -> {
                    ((Runnable) invocation.getArguments()[0]).run();
                    return null;
                }
        ).when(taskExecutor).execute(any(Runnable.class));
        when(customerApiClient.retrieveCustomerDetails(anyString())).thenReturn(forData());
        when(orderRepository.findByProductId(Mockito.anyLong())).thenReturn(Optional.empty());
        when(orderRepository.save(any())).thenReturn(forOrderEntity());
        Long response = orderService.createOrder(formOrderDto());
        assertNotNull(response);
    }

    @Test
    public void test_CreateOrderWithInvalidEmailId() {
        doAnswer(
                (InvocationOnMock invocation) -> {
                    ((Runnable) invocation.getArguments()[0]).run();
                    return null;
                }
        ).when(taskExecutor).execute(any(Runnable.class));
        when(customerApiClient.retrieveCustomerDetails(anyString())).thenThrow(new OrderException(ErrorCodes.NOT_FOUND, "emailId not found"));
        assertThrows(OrderException.class, () -> orderService.createOrder(formOrderDto()));
    }

    @Test
    public void test_CreateOrderWithInvalidProductId() {
        doAnswer(
                (InvocationOnMock invocation) -> {
                    ((Runnable) invocation.getArguments()[0]).run();
                    return null;
                }
        ).when(taskExecutor).execute(any(Runnable.class));
        when(customerApiClient.retrieveCustomerDetails(anyString())).thenReturn(forData());
        when(orderRepository.findByProductId(Mockito.anyLong())).thenReturn(Optional.of(forOrderEntity()));
        assertThrows(OrderException.class, () -> orderService.createOrder(formOrderDto()));
    }

    private Customer forData() {
        return Customer.builder()
                .email("amitv@mail.com")
                .firstName("Amit")
                .lastName("Vs")
                .build();
    }

    private OrderDTO formOrderDto() {
        return OrderDTO.builder()
                .email("amitv@mail.com")
                .productId(1234L).build();
    }
}
