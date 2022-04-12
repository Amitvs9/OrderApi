package com.vz.orderapi.services;

import com.vz.orderapi.dto.Customer;
import com.vz.orderapi.dto.OrderDTO;
import com.vz.orderapi.entities.OrderEntity;
import com.vz.orderapi.errors.ErrorCodes;
import com.vz.orderapi.errors.OrderException;
import com.vz.orderapi.repos.OrderRepository;
import com.vz.orderapi.webserviceClient.CustomerApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * OrderExecutor class
 * Service class to execute order request
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerApiClient customerApiClient;
    private final Executor taskExecutor;

    /**
     * create Order for given productId
     *
     * @param orderDTO orderDto
     * @return OrderId
     */
    public Long createOrder(OrderDTO orderDTO) {
        log.info("Create Order for emailId: {} ProductId: {}", orderDTO.getEmail(), orderDTO.getProductId());
        CompletableFuture<Customer> customerDetails = CompletableFuture.supplyAsync(() ->
                customerApiClient.retrieveCustomerDetails(orderDTO.getEmail()), taskExecutor);

        CompletableFuture<Long> productId = CompletableFuture.supplyAsync(() ->
                productExistIntoDB(orderDTO.getProductId()), taskExecutor);

        CompletableFuture.allOf(customerDetails, productId);
        try {
            Customer customer = customerDetails.get();
            OrderEntity orderEntity = OrderEntity.builder()
                    .email(customer.getEmail())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .productId(productId.get()).build();
            return orderRepository.save(orderEntity).getOrderId();
        } catch (InterruptedException | ExecutionException exception) {
            log.error("exception occurred while creating order");
            if (exception.getCause() instanceof OrderException)
                throw (OrderException) exception.getCause();
            else
                log.error(exception.getLocalizedMessage());
        }
        return null;
    }

    /**
     * gets all the {@link com.vz.orderapi.entities.OrderEntity} from database with pagination support
     *
     * @param page page number
     * @param size number of results in one page
     * @return list of {@link com.vz.orderapi.entities.OrderEntity}
     */
    public List<OrderEntity> getAllOrders(int page, int size) {
        List<OrderEntity> orderEntities = orderRepository.findAll(PageRequest.of(page, size)).getContent();
        if (CollectionUtils.isEmpty(orderEntities)) {
            log.error("No record found");
            throw new OrderException(ErrorCodes.NOT_FOUND, "No Record found");
        }
        return orderEntities;
    }

    private long productExistIntoDB(long productId) {
        log.info("productId to check into db : {}", productId);
        Optional<OrderEntity> orderEntity = orderRepository.findByProductId(productId);
        if (orderEntity.isPresent()) {
            log.error("Order already created for given productId : {}", productId);
            throw new OrderException(ErrorCodes.INVALID_ORDER, String.format("ProductId : %s is already exist", productId));
        }
        return productId;
    }
}
