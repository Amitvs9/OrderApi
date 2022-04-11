package com.vz.orderapi.dto;

/**
 * OrderDTO
 * DTO Object for request to create order
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
public class OrderDTO {

    @Positive
    private final Long productId;
    @Email
    private final String email;
}
