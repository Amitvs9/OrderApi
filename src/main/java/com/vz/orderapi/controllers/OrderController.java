package com.vz.orderapi.controllers;


import com.vz.orderapi.dto.OrderDTO;
import com.vz.orderapi.entities.OrderEntity;
import com.vz.orderapi.services.OrderExecutor;
import com.vz.orderapi.util.Constant;
import lombok.RequiredArgsConstructor;
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

/**
 * OrderController
 * Controller to serve all request coming for order
 */
@RestController
@RequestMapping(path = Constant.ORDER)
@RequiredArgsConstructor
public class OrderController {

    private final OrderExecutor orderExecutor;

    /**
     * Gets all the order from database
     * @param page page defaults to 0
     * @param size size of page defaults to 10
     * @return List of all orders in page
     */
    @GetMapping(Constant.VERSION)
    public ResponseEntity<List<OrderEntity>> getAllOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<OrderEntity> orderEntities = orderExecutor.getAllOrders(page,size);
        return ResponseEntity.status( HttpStatus.OK).body(orderEntities);
    }

    /**
     * Create Order for given productId
     *
     * @param orderDTO DTO object containing emailId and productId
     * @return generated orderId foe created order.
     */
    @PostMapping(Constant.VERSION)
    public ResponseEntity<OrderEntity> createOrder(@RequestBody @Valid OrderDTO orderDTO) {
        OrderEntity orderId= orderExecutor.createOrder(orderDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }
}
