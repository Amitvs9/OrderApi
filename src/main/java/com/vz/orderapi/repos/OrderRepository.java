package com.vz.orderapi.repos;

import com.vz.orderapi.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * OrderRepository
 * repository class to perform database operation through JPA
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    Optional<OrderEntity> findByProductId(long productId);
}
