package com.vz.orderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * Response class
 * class to hold Api all customer details
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Response {
    private List<Data> data;
}
