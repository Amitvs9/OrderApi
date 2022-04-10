package com.av.orderapi.services;

import com.av.orderapi.dto.Data;
import com.av.orderapi.dto.OrderDTO;
import com.av.orderapi.entities.OrderEntity;
import com.av.orderapi.errors.ErrorCodes;
import com.av.orderapi.errors.OrderException;
import com.av.orderapi.repos.OrderRepository;
import com.av.orderapi.webserviceClient.CustomerApiClient;
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
    public Long createOrder(OrderDTO orderDTO) {

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
            return saveOrderEntity(orderEntity).getOrderId();
        }
        catch (InterruptedException | ExecutionException exception) {
            try {
                throw exception.getCause() instanceof OrderException ? exception.getCause() : exception;
            }catch(RuntimeException orderException) {
                throw orderException;
            }
            catch(Throwable impossible) {
                throw new OrderException(ErrorCodes.UNKNOWN, "Something went wrong can not create Order");
            }
        }
    }

    /**
     * gets all the {@link com.av.orderapi.entities.OrderEntity} from database with pagination support
     *
     * @param page page number
     * @param size number of results in one page
     * @return list of {@link com.av.orderapi.entities.OrderEntity}
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
        log.info("email id: {}", email);
        return CompletableFuture.completedFuture(customerApiClient.retrieveCustomerDetails(email));
    }

    @Async("asyncExecutor")
    private CompletableFuture<Long> getProductDetail(long productId)throws OrderException{
        log.info("productId : {}", productId);
        return CompletableFuture.completedFuture(productExistIntoDB(productId));
    }

    private OrderEntity saveOrderEntity(OrderEntity orderEntity){
        return orderRepository.save(orderEntity);
    }

    private long productExistIntoDB(long productId) {
        Optional<OrderEntity> orderEntity = orderRepository.findByProductId(productId);
        if (orderEntity.isPresent()) {
            throw new OrderException(ErrorCodes.INVALID_ORDER, String.format("ProductId : %s is already exist", productId));
        }
        return productId;
    }
}
