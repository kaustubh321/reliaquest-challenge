package com.example.rqchallenge.constants;

public class APIConstants {
    public static final String BASE_URL = "https://dummy.restapiexample.com";
    public static final String VERSION_1 = "v1";
    public static final String BASE_API_URL = String.format("%s/api/%s/",BASE_URL,VERSION_1);
    public static final String GET_ALL_EMPLOYEES_URL = "employees";
    public static final String GET_EMPLOYEE_BY_ID_URL = "employee/%s";
    public static final String CREATE_EMPLOYEE_URL = "create";
    public static final String DELETE_EMPLOYEE_URL = "delete/%s";
}
