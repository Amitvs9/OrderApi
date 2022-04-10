package com.vz.orderapi.errors;

import lombok.Getter;
import lombok.Setter;

/**
 *  OrderException
 *  RuntimeException to handle expected exceptions and Map them to {@link ApiError}
 */
@Getter
@Setter
public class OrderException extends RuntimeException  {

    private final ErrorCodes errorCode;
    private final String message;

    public OrderException(ErrorCodes errorCode, String message){
        super(message);
        this.errorCode= errorCode;
        this.message=message;
    }

}
