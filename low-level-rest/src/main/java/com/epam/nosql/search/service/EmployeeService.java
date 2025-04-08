package com.epam.nosql.search.service;

import com.epam.nosql.search.dto.Employee;
import com.epam.nosql.search.dto.QueryType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    Employee getEmployeeById(String id) throws IOException;

    List<Employee> getAllEmployees() throws IOException;

    String createEmployee(String id, Employee employee) throws IOException;

    String deleteEmployee(String id) throws IOException;

    List<Employee> searchEmployees(QueryType queryType, String field, String value) throws IOException;

    Map<String, Object> performAggregation(String aggregationName, String metricType, String metricField) throws IOException;
}
