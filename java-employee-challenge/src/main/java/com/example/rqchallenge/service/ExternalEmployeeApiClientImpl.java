package com.example.rqchallenge.service;

import com.example.rqchallenge.commonutils.RestExecutorService;
import com.example.rqchallenge.constants.APIConstants;
import com.example.rqchallenge.dto.BaseResponse;
import com.example.rqchallenge.dto.EmployeeResponseData;
import com.example.rqchallenge.dto.EmployeesResponseData;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Class contains logic to interact with HttpClient for CRUD employee operations to external world
 */
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ExternalEmployeeApiClientImpl implements IExternalEmployeeAPIClient{
    private final RestExecutorService restExecutorService;

    @Override
    public ResponseEntity<EmployeesResponseData> getAllEmployees() {
        return restExecutorService.execute(APIConstants.BASE_API_URL + APIConstants.GET_ALL_EMPLOYEES_URL,
                getHttpEntity(), HttpMethod.GET, EmployeesResponseData.class);
    }

    @Override
    public ResponseEntity<EmployeeResponseData> getEmployeeById(String id) {
        return restExecutorService.execute(APIConstants.BASE_API_URL + String.format(APIConstants.GET_EMPLOYEE_BY_ID_URL, id),
                getHttpEntity(), HttpMethod.GET, EmployeeResponseData.class);
    }

    @Override
    public ResponseEntity<EmployeeResponseData> createEmployee(Map<String, Object> employeeInput) {
        return restExecutorService.execute(APIConstants.BASE_API_URL + APIConstants.CREATE_EMPLOYEE_URL,
                getHttpEntity(employeeInput), HttpMethod.POST, EmployeeResponseData.class);
    }

    @Override
    public ResponseEntity<BaseResponse> deleteEmployeeById(String id) {
        return restExecutorService.execute(APIConstants.BASE_API_URL + String.format(APIConstants.DELETE_EMPLOYEE_URL, id),
                getHttpEntity(), HttpMethod.DELETE, BaseResponse.class);
    }

    /**
     * @return HttpEntity with only headers
     */
    private HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity(headers);
    }

    /**
     * @param input
     * @return HttpEntity with header and input payload body
     */
    private HttpEntity getHttpEntity(Map<String, Object> input) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity(input, headers);
    }
}
