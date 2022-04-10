package com.av.orderapi.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ORDERS")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long orderId;
    private Long productId;
    private String firstName;
    private String lastName;
    private String email;
}
