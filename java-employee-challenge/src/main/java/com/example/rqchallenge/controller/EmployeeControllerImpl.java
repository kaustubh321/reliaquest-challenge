package com.example.rqchallenge.controller;

import com.example.rqchallenge.aspects.Timed;
import com.example.rqchallenge.dto.Employee;
import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.service.IEmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeControllerImpl implements IEmployeeController {

    private final IEmployeeService iEmployeeService;

    /**
     * @return returns list of all employee objects
     */
    @Override
    @Timed
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return new ResponseEntity<>(iEmployeeService.getAllEmployees(), HttpStatus.OK);
    }

    /**
     * @param searchString
     * @return returns list of all employee objects which contains or matches searchString
     */
    @Override
    @Timed
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return new ResponseEntity<>(iEmployeeService.getEmployeesByNameSearch(searchString), HttpStatus.OK);
    }

    /**
     * @param id
     * @return Employee object with specified Id
     */
    @Override
    @Timed
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return new ResponseEntity<>(iEmployeeService.getEmployeeById(id), HttpStatus.OK);
    }

    /**
     * @return highest salary among all employees
     */
    @Override
    @Timed
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return new ResponseEntity<>(iEmployeeService.getHighestSalaryOfEmployees(), HttpStatus.OK);
    }

    /**
     * @return names of employees with top 10 highest salaries
     */
    @Override
    @Timed
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return new ResponseEntity<>(iEmployeeService.getTopTenHighestEarningEmployeeNames(), HttpStatus.OK);
    }

    /**
     * @param employeeInputData map containing name, age, salary as mandatory fields and profileImage as fields
     * @return Employee object which is created using input
     */
    @Override
    @Timed
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInputData) {
        return new ResponseEntity<>(iEmployeeService.createEmployee(employeeInputData), HttpStatus.CREATED);
    }

    /**
     * @param id
     * @return message from operation if it is executed successfully or not
     */
    @Override
    @Timed
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return new ResponseEntity<>(iEmployeeService.deleteEmployeeById(id), HttpStatus.OK);
    }
}
