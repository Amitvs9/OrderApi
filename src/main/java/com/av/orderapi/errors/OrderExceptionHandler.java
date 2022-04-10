package com.av.orderapi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * OrderExceptionHandler
 *  To handle expected exceptions in Application and Map them to Application specific {@link com.av.orderapi.errors.ErrorCodes} which are translated to {@link ApiError}
 */
@ControllerAdvice
public class OrderExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {OrderException.class})
    private ResponseEntity<Object> handleException(OrderException exception){
        switch (exception.getErrorCode()){
            case NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND,exception.getMessage()));
            case INVALID_ORDER:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST,exception.getMessage()));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,exception.getMessage()));
        }
    }

    //@Override
   /* protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(CODE, AccountErrorConstants.ERROR_CODE_E1003);
        body.put(DESCRIPTION, AccountErrorConstants.ERROR_DESC_E1003);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        body.put(ERRORS, errors);
        return new ResponseEntity<>(body, headers, status);
    }
*/
}
