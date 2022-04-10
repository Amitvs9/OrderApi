package com.av.orderapi.controllers;


import com.av.orderapi.dto.OrderDTO;
import com.av.orderapi.entities.OrderEntity;
import com.av.orderapi.services.OrderExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderExecutor orderExecutor;

    /**
     * Gets all the order from database
     * @param page page defaults to 0
     * @param size size of page defaults to 10
     * @return List of all orders in page
     */
    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAllOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<OrderEntity> orderEntities = orderExecutor.getAllOrders(page,size);
        return ResponseEntity.status( HttpStatus.OK).body(orderEntities);
    }

    /**
     * Create Order for given productId
     *
     * @param orderDTO DTO object containing productId and sellingQuantity
     * @return updated product with recalculated available quantity
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
        Long orderId= orderExecutor.createOrder(orderDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }
}
