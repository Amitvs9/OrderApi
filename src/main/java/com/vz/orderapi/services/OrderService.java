//package com.av.orderapi.services;
//
//import com.av.orderapi.dto.Data;
//import com.av.orderapi.entities.OrderEntity;
//import com.av.orderapi.errors.ErrorCodes;
//import com.av.orderapi.errors.OrderException;
//import com.av.orderapi.repos.OrderRepository;
//import com.av.orderapi.webserviceClient.CustomerApiClient;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.CompletableFuture;
//
//@RequiredArgsConstructor
//@Slf4j
//@Service
//public class OrderService {
//
//    private final OrderRepository orderRepository;
//    private final CustomerApiClient customerApiClient;
//
//    /**
//     * get customer details for given emailId
//     *
//     * @param email email
//     * @return CompletableFuture of Data
//     * @throws OrderException OrderException
//     */
//    @Async("asyncExecutor")
//    public CompletableFuture<Data> getCustomerDetails(String email) throws OrderException {
//        log.info("email id: {}", email);
//        return CompletableFuture.completedFuture(customerApiClient.retrieveCustomerDetails(email));
//    }
//
//    /**
//     * check ProductId exist into database
//     *
//     * @param productId productId
//     * @return CompletableFuture of productId
//     * @throws OrderException OrderException
//     */
//    @Async("asyncExecutor")
//    public CompletableFuture<Long> getProductDetail(long productId)throws OrderException{
//        log.info("productId : {}", productId);
//        return CompletableFuture.completedFuture(productExistIntoDB(productId));
//    }
//
//    /**
//     * save orderEntity into database
//     *
//     * @param orderEntity orderEntity
//     * @return OrderEntity
//     */
//    public OrderEntity saveOrderEntity(OrderEntity orderEntity){
//       return orderRepository.save(orderEntity);
//    }
//
//    /**
//     * gets all the {@link com.av.orderapi.entities.OrderEntity} from database with pagination support
//     *
//     * @param page page number
//     * @param size number of results in one page
//     * @return list of {@link com.av.orderapi.entities.OrderEntity}
//     */
//    public List<OrderEntity> getAllOrders(int page, int size) {
//        List<OrderEntity> orderEntities = orderRepository.findAll(PageRequest.of(page, size)).getContent();
//        if (CollectionUtils.isEmpty( orderEntities )) {
//            throw new OrderException(ErrorCodes.NOT_FOUND, "No Record found" );
//        }
//        return orderEntities;
//    }
//
//    private long productExistIntoDB(long productId) {
//        Optional<OrderEntity> orderEntity = orderRepository.findByProductId( productId );
//        if (orderEntity.isPresent()) {
//            throw new OrderException(ErrorCodes.INVALID_ORDER, String.format("ProductId : %s is already exist", productId));
//        }
//        return productId;
//    }
//}
