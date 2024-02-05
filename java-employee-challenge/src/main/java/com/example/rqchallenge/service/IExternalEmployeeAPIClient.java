package com.example.rqchallenge.service;

import com.example.rqchallenge.dto.BaseResponse;
import com.example.rqchallenge.dto.EmployeeResponseData;
import com.example.rqchallenge.dto.EmployeesResponseData;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IExternalEmployeeAPIClient {
    ResponseEntity<EmployeesResponseData> getAllEmployees();
    ResponseEntity<EmployeeResponseData> getEmployeeById(String id);
    ResponseEntity<EmployeeResponseData> createEmployee(Map<String, Object> employeeInput);
    ResponseEntity<BaseResponse> deleteEmployeeById(String id);
}
