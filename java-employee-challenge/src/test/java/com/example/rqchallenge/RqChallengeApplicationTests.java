package com.example.rqchallenge;

import com.example.rqchallenge.dto.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
class RqChallengeApplicationTests {

    @LocalServerPort
    int randomServerPort;

    private static final String LOCAL_HOST_BASE_URL= "http://localhost:";
    private static RestTemplate restTemplate;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setup() {
        restTemplate = new RestTemplate();
        objectMapper = new ObjectMapper();
    }

    void contextLoads() {
    }
    private StringBuilder getBaseApiUrl() {
        return new StringBuilder().append(LOCAL_HOST_BASE_URL).append(randomServerPort);
    }

    @Test
    @DisplayName("Invalid URL test")
    public void testInvalidUrl() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().append("/abcd").toString());
        assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.getForEntity(uri, String.class));
    }

    @Test
    @DisplayName("Get all employees list")
    public void testGetAllEmployeesAPI() throws URISyntaxException, JsonProcessingException {
        URI uri = new URI(getBaseApiUrl().toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<List<Employee>>(){});

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        List<Employee> employeeList = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(employeeList);
        Assertions.assertEquals(24, employeeList.size());
    }

    @Test
    @DisplayName("Search employee API with search String")
    public void testSearchEmployeeBySearchString() throws URISyntaxException, JsonProcessingException {
        URI uri = new URI(getBaseApiUrl().append("/search/tig").toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<List<Employee>>(){});

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        List<Employee> employeeList = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(employeeList);
        employeeList.forEach(emp->{
            Assertions.assertTrue(emp.getName().toLowerCase().contains("tig"));
        });
    }
    @Test
    @DisplayName("Fetch employee details API with ID")
    public void testFetchEmployeeWithValidID() throws URISyntaxException, JsonProcessingException {
        URI uri = new URI(getBaseApiUrl().append("/2").toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<Employee>(){});
        Employee expectedEmployee = new Employee(2L, "Garrett Winters", 170750, 63, "");

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Employee actualEmployee = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(actualEmployee);
        Assertions.assertEquals(expectedEmployee, actualEmployee);
    }

    @Test
    @DisplayName("Fetch employee details API with Invalid ID")
    public void testFetchEmployeeWithInvalidID() throws URISyntaxException, JsonProcessingException {
        URI uri = new URI(getBaseApiUrl().append("/212122").toString());

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.getForEntity(uri, String.class));
        //System.out.println(ex.getMessage());
        Assertions.assertTrue(ex.getMessage().contains("No record found for employee with id 212122."));
    }

    @Test
    @DisplayName("highest salary of employee among all test")
    public void testHighestSalaryOfEmployeesAPI() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().append("/highestSalary").toString());

        ResponseEntity<Integer> result = restTemplate.getForEntity(uri, Integer.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(725000, result.getBody());
    }

    @Test
    @DisplayName("Top ten highest earning employee list API test")
    public void testTopTenHighestEarningEmployeeNames() throws URISyntaxException, JsonProcessingException {
        URI uri = new URI(getBaseApiUrl().append("/topTenHighestEarningEmployeeNames").toString());
        ObjectReader objectReader = objectMapper.reader().forType(new TypeReference<List<String>>(){});
        List<String> expectedNameList = Arrays.asList("Paul Byrd", "Yuri Berry",
                "Charde Marshall", "Cedric Kelly", "Tatyana Fitzpatrick", "Brielle Williamson",
                "Jenette Caldwell", "Quinn Flynn", "Rhona Davidson", "Tiger Nixon");

        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        List<String> actualNameList = objectReader.readValue(result.getBody());
        Assertions.assertNotNull(actualNameList);
        Assertions.assertEquals(10, actualNameList.size());
        Assertions.assertEquals(expectedNameList, actualNameList);
    }


    @Test
    @DisplayName("Create employee API test with all valid inputs")
    public void testCreateEmployee() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().toString());

        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("name", "Harry Potter");
        inputRequestPayload.put("salary", 204500);
        inputRequestPayload.put("age", 26);

        ResponseEntity<Employee> result = restTemplate.postForEntity(uri, inputRequestPayload, Employee.class);

        //assertions
        Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
        Assertions.assertNotNull(result.getBody());
        Employee actualEmployee = result.getBody();
        Assertions.assertNotNull(actualEmployee);
        Assertions.assertNotNull(actualEmployee.getId());
        Assertions.assertEquals("Harry Potter", actualEmployee.getName());
        Assertions.assertEquals(26, actualEmployee.getAge());
        Assertions.assertEquals(204500, actualEmployee.getSalary());
    }

    @Test
    @DisplayName("Create employee name missing in input")
    public void testCreateEmployeeNameMissing() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().toString());
        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("salary", 204500);
        inputRequestPayload.put("age", 26);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        Assertions.assertTrue(ex.getMessage().contains("Name field is mandatory in create employee request."));
    }

    @Test
    @DisplayName("Create employee age missing API test")
    public void testCreateEmployeeAgeMissing() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().toString());
        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("name", "Harry Potter");
        inputRequestPayload.put("salary", 204500);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        Assertions.assertTrue(ex.getMessage().contains("Age field is mandatory in create employee request."));
    }

    @Test
    @DisplayName("Create employee salary missing API test")
    public void testCreateEmployeeSalaryMissing() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().toString());
        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("name", "Harry Potter");
        inputRequestPayload.put("age", 26);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        Assertions.assertTrue(ex.getMessage().contains("Salary field is mandatory in create employee request."));
    }

    @Test
    @DisplayName("Create employee invalid salary in input")
    public void testCreateEmployeeInvalidSalary() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().toString());
        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("salary", -1);
        inputRequestPayload.put("age", 26);
        inputRequestPayload.put("name", "test");

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        Assertions.assertTrue(ex.getMessage().contains("Invalid salary field is passed in create employee request."));
    }
    @Test
    @DisplayName("Create employee invalid age in input")
    public void testCreateEmployeeInvalidAge() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().toString());
        Map<String, Object> inputRequestPayload = new HashMap<>();
        inputRequestPayload.put("salary", 1000);
        inputRequestPayload.put("age", -1);
        inputRequestPayload.put("name", "test");

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                ()-> restTemplate.postForEntity(uri, inputRequestPayload, Employee.class));

        Assertions.assertTrue(ex.getMessage().contains("Invalid age field is passed in create employee request."));
    }

    @Test
    @DisplayName("Delete employee by Id API test")
    public void testDeleteEmployeeById() throws URISyntaxException {
        URI uri = new URI(getBaseApiUrl().append("/23").toString());
        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.DELETE,null,String.class);
        Assert.assertEquals("Successfully! Record has been deleted", result.getBody());
    }




}
