package com.av.orderapi.webserviceClient;

import com.av.orderapi.dto.Response;
import com.av.orderapi.dto.Data;
import com.av.orderapi.errors.ErrorCodes;
import com.av.orderapi.errors.OrderException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.av.orderapi.util.Constant.PAGE;
import static com.av.orderapi.util.Constant.PERPAGE;
@RequiredArgsConstructor
@Component
public class CustomerApiClient {

    private final RestTemplate apiRestTemplate;

    @Value("${order.api.endpoint}")
    private String endpoint;
    @Value("${order.api.page}")
    private Integer page;
    @Value("${order.api.perPage}")
    private Integer perPage;

    public Data retrieveCustomerDetails(String email){
        Map<String, Object> params = new HashMap<>();
        params.put(PAGE, page);
        params.put(PERPAGE, perPage);
        try {
            Response apiResponse = apiRestTemplate.getForEntity(endpoint, Response.class, params).getBody();
            if (apiResponse != null && apiResponse.getData() != null) {
                return apiResponse.getData().stream()
                        .filter( data -> StringUtils.equals(email, data.getEmail())).findFirst()
                        .orElseThrow(() -> new OrderException( ErrorCodes.NOT_FOUND, String.format("EmailId : %s is not Found", email)));
            }
        }catch (OrderException exception){
            throw exception;
        }catch (Exception exception){
            throw new OrderException(ErrorCodes.UNKNOWN, "Exception occurred while calling Api");
        }
        return null;
    }
}
