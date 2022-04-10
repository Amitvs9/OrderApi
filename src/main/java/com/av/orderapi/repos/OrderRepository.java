package com.av.orderapi.repos;

import com.av.orderapi.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    Optional<OrderEntity> findByProductId(long productId);
}
