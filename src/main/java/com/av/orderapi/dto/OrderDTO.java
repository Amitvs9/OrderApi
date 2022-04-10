package com.av.orderapi.dto;

/**
 * OrderDTO
 * DTO Object for request to create order
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @Positive
    private Long productId;
    @Email
    private String email;
}
