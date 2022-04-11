package com.vz.orderapi.webserviceClient;

import com.vz.orderapi.dto.Response;
import com.vz.orderapi.dto.Data;
import com.vz.orderapi.errors.ErrorCodes;
import com.vz.orderapi.errors.OrderException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.vz.orderapi.util.Constant.PAGE;
import static com.vz.orderapi.util.Constant.PERPAGE;
/**
 * CustomerApiClient class
 * class to call Api
 */
@Component
public class CustomerApiClient {

    private final RestTemplate apiRestTemplate;
    private final String endpoint;
    private final Integer page;
    private final Integer perPage;

    public CustomerApiClient(RestTemplate apiRestTemplate,
                             @Value("${order.api.endpoint}") String endpoint,
                             @Value("${order.api.page}") Integer page,
                             @Value("${order.api.perPage}") Integer perPage) {
        this.apiRestTemplate = apiRestTemplate;
        this.endpoint = endpoint;
        this.page = page;
        this.perPage = perPage;
    }

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
