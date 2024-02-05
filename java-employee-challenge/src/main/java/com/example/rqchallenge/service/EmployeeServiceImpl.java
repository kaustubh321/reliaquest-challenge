package com.example.rqchallenge.service;


import com.example.rqchallenge.constants.MessageConstants;
import com.example.rqchallenge.dto.BaseResponse;
import com.example.rqchallenge.dto.Employee;
import com.example.rqchallenge.dto.EmployeeResponseData;
import com.example.rqchallenge.dto.EmployeesResponseData;
import com.example.rqchallenge.exceptions.OperationFailedException;
import com.example.rqchallenge.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeServiceImpl implements IEmployeeService {

    private final IExternalEmployeeAPIClient iExternalEmployeeAPIClient;
    private final ValidatorService validatorService;

    /**
     * @return returns list of all employee objects
     */
    @Override
    public List<Employee> getAllEmployees() {
        try {
            ResponseEntity<EmployeesResponseData> responseEntity = iExternalEmployeeAPIClient.getAllEmployees();
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode()) || Objects.isNull(responseEntity.getBody())) {
                throw new OperationFailedException(MessageConstants.FETCH_EMP_LIST_ERROR_MESSAGE);
            }
            return responseEntity.getBody().getData();
        } catch (Exception ex) {
            log.error("Error occurred while fetching all employees : ", ex);
            throw ex;
        }
    }


    /**
     * @param searchString
     * @return returns list of all employee objects which contains or matches searchString
     */
    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        validatorService.validateEmployeeNameSearchString(searchString);

        ResponseEntity<EmployeesResponseData> responseEntity = iExternalEmployeeAPIClient.getAllEmployees();
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode()) || Objects.isNull(responseEntity.getBody())) {
            log.error("Failed to get employee list to get employees by name search.");
            throw new OperationFailedException(MessageConstants.FETCH_EMP_LIST_ERROR_MESSAGE);
        }
        return Optional.ofNullable(responseEntity.getBody().getData())
                .orElseThrow(() -> new OperationFailedException(String.format(MessageConstants.SEARCH_BY_NAME_OP_FAILED_ERROR_MESSAGE, searchString)))
                .stream()
                .filter(e -> StringUtils.containsIgnoreCase(e.getName(), searchString))
                .collect(Collectors.toList());
    }

    /**
     * @param id
     * @return Employee object with specified Id
     */
    @Override
    public Employee getEmployeeById(String id) {
        //we can validate id here, like id should be always positive integer etc.
        try {
            ResponseEntity<EmployeeResponseData> responseEntity = iExternalEmployeeAPIClient.getEmployeeById(id);
            if (!HttpStatus.OK.equals(responseEntity.getStatusCode())
                    || Objects.isNull(responseEntity.getBody())
                    || Objects.isNull(responseEntity.getBody().getData())) {
                throw new ResourceNotFoundException(String.format(MessageConstants.EMP_NOT_FOUND_ERROR_MESSAGE, id));
            }
            return responseEntity.getBody().getData();
        } catch (Exception ex) {
            log.error("Exception occurred while fetching employee with id {} :", id, ex);
            throw ex;
        }
    }

    /**
     * @return highest salary among all employees
     */
    @Override
    public Integer getHighestSalaryOfEmployees() {
        ResponseEntity<EmployeesResponseData> responseEntity = iExternalEmployeeAPIClient.getAllEmployees();
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode()) || Objects.isNull(responseEntity.getBody())) {
            log.error("Failed to get employee list to calculate max salary");
            throw new OperationFailedException(MessageConstants.FETCH_EMP_LIST_ERROR_MESSAGE);
        }
        return Optional.ofNullable(responseEntity.getBody().getData())
                .orElseThrow(() -> new OperationFailedException(MessageConstants.CALC_HIGHEST_SALARY_OP_FAILED_ERROR_MESSAGE))
                .stream()
                .map(Employee::getSalary)
                .reduce(0, Integer::max);
    }

    /**
     * @return names of employees with top 10 highest salaries
     */
    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        ResponseEntity<EmployeesResponseData> responseEntity = iExternalEmployeeAPIClient.getAllEmployees();
        if (!HttpStatus.OK.equals(responseEntity.getStatusCode()) || Objects.isNull(responseEntity.getBody())) {
            log.error("Failed to get employee names with top 10 highest salaries");
            throw new OperationFailedException(MessageConstants.FETCH_EMP_LIST_ERROR_MESSAGE);
        }
        return Optional.ofNullable(responseEntity.getBody().getData())
                .orElseThrow(() -> new OperationFailedException(MessageConstants.TOP_TEN_HIGHEST_EARNING_OP_FAILED_ERROR_MESSAGE))
                .stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());

    }

    /**
     * @param employeeInputData
     * @return Employee object which is created using input
     */
    @Override
    public Employee createEmployee(Map<String, Object> employeeInputData) {
        validatorService.validateCreateEmployeeInputPayload(employeeInputData);
        ResponseEntity<EmployeeResponseData> responseEntity = iExternalEmployeeAPIClient.createEmployee(employeeInputData);

        if (Objects.isNull(responseEntity.getBody()) || Objects.isNull(responseEntity.getBody().getData())) {
            log.error("Failed to create employee record with input {}", employeeInputData);
            throw new OperationFailedException(String.format(MessageConstants.CREATE_EMPLOYEE_FAILED_ERROR_MESSAGE, employeeInputData.get("name")));
        }
        return responseEntity.getBody().getData();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public String deleteEmployeeById(String id) {
        //we can validate id here, like id should be always positive integer.

            /*
                1. Do we need to check if employee exist before deleting it?
                if yes, then we need to fetch all employees and then check ID if it exists or not

                https://dummy.restapiexample.com/api/v1/employee/85600
                this API is always returning success but data is always being returned null
                {
                    "status": "success",
                    "data": null,
                    "message": "Successfully! Record has been fetched."
                }

                2. if invalid id is passed to deleteAPI, it will also return success.
                    it is not under our control
           */
        ResponseEntity<BaseResponse> responseEntity = iExternalEmployeeAPIClient.deleteEmployeeById(id);
        if (responseEntity.getStatusCode().isError()) {
            return String.format(MessageConstants.DELETE_EMPLOYEE_FAILED_ERROR_MESSAGE, id);
        }
        return responseEntity.getBody().getMessage();
    }
}
