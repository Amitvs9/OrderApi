package com.vz.orderapi.services;

import com.vz.orderapi.dto.Data;
import com.vz.orderapi.dto.OrderDTO;
import com.vz.orderapi.entities.OrderEntity;
import com.vz.orderapi.errors.ErrorCodes;
import com.vz.orderapi.errors.OrderException;
import com.vz.orderapi.repos.OrderRepository;
import com.vz.orderapi.webserviceClient.CustomerApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
/**
 * OrderExecutor class
 * Service class to execute order request
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class OrderExecutor {

    private final OrderRepository orderRepository;
    private final CustomerApiClient customerApiClient;

    /**
     * create Order for given productId
     *
     * @param orderDTO orderDto
     * @return OrderId
     */
    public OrderEntity createOrder(OrderDTO orderDTO) {

        try {
            CompletableFuture<Data> customerDetails = getCustomerDetails(orderDTO.getEmail() );
            CompletableFuture<Long> productId = getProductDetail(orderDTO.getProductId() );

            CompletableFuture.allOf( customerDetails, productId );
            Data customer = customerDetails.get();
            OrderEntity orderEntity = OrderEntity.builder()
                    .email(customer.getEmail())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .productId(productId.get()).build();
            return orderRepository.save(orderEntity);
        }
        catch (InterruptedException | ExecutionException exception) {
            log.error("exception occurred while creating order");
            try {
                throw exception.getCause() instanceof OrderException ? exception.getCause() : exception;
            }catch(RuntimeException orderException) {
                throw orderException;
            }
            catch(Throwable impossible) {
                throw new OrderException( ErrorCodes.UNKNOWN, "Something went wrong can not create Order");
            }
        }
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
            throw new OrderException(ErrorCodes.NOT_FOUND, "No Record found" );
        }
        return orderEntities;
    }


    @Async("asyncExecutor")
    private CompletableFuture<Data> getCustomerDetails(String email) throws OrderException {
        log.info("email-id to validate on api : {}", email);
        return CompletableFuture.completedFuture(customerApiClient.retrieveCustomerDetails(email));
    }

    @Async("asyncExecutor")
    private CompletableFuture<Long> getProductDetail(long productId)throws OrderException{
        log.info("productId to create order : {}", productId);
        return CompletableFuture.completedFuture(productExistIntoDB(productId));
    }

    private long productExistIntoDB(long productId) {
        Optional<OrderEntity> orderEntity = orderRepository.findByProductId(productId);
        if (orderEntity.isPresent()) {
            log.error("Order already created for given productId : {}", productId);
            throw new OrderException(ErrorCodes.INVALID_ORDER, String.format("ProductId : %s is already exist", productId));
        }
        return productId;
    }
}
