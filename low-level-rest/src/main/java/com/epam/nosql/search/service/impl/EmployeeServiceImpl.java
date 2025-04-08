package com.epam.nosql.search.service.impl;

import com.epam.nosql.search.dto.Employee;
import com.epam.nosql.search.dto.QueryType;
import com.epam.nosql.search.service.EmployeeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final RestClient client;
    private final ObjectMapper objectMapper;

    private static final String INDEX = "employees";

    @Autowired
    public EmployeeServiceImpl(RestClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public Employee getEmployeeById(String id) throws IOException {
        Request request = new Request("GET", "/" + INDEX + "/_doc/" + id);
        Response response = client.performRequest(request);
        JsonNode source = objectMapper.readTree(response.getEntity().getContent()).path("_source");

        return objectMapper.readValue(source.toString(), Employee.class);
    }

    @Override
    public List<Employee> getAllEmployees() throws IOException {
        Request request = new Request("GET", "/" + INDEX + "/_search");
        Response response = client.performRequest(request);
        JsonNode hits = objectMapper.readTree(response.getEntity().getContent()).path("hits").path("hits");

        List<Employee> employees = new ArrayList<>();
        for (JsonNode hit : hits) {
            employees.add(objectMapper.readValue(hit.path("_source").toString(), Employee.class));
        }
        return employees;
    }

    @Override
    public String createEmployee(String id, Employee employee) throws IOException {
        Request request = new Request("PUT", "/" + INDEX + "/_doc/" + id);
        request.setJsonEntity(objectMapper.writeValueAsString(employee));
        client.performRequest(request);
        return "Created employee with ID " + id;
    }

    @Override
    public String deleteEmployee(String id) throws IOException {
        Request request = new Request("DELETE", "/" + INDEX + "/_doc/" + id);
        client.performRequest(request);
        return "Deleted employee with ID " + id;
    }

    @Override
    public List<Employee> searchEmployees(QueryType queryType, String field, String value) throws IOException {
        Request request = new Request("GET", "/" + INDEX + "/_search");
        String query = String.format("""
            {
              "query": {
                "%s": {
                  "%s": "%s"
                }
              }
            }
            """, queryType, field, value);
        request.setJsonEntity(query);
        Response response = client.performRequest(request);
        JsonNode hits = objectMapper.readTree(response.getEntity().getContent()).path("hits").path("hits");

        List<Employee> employees = new ArrayList<>();
        for (JsonNode hit : hits) {
            employees.add(objectMapper.readValue(hit.path("_source").toString(), Employee.class));
        }
        return employees;
    }

    @Override
    public Map<String, Object> performAggregation(String aggregationName, String metricType, String metricField) throws IOException {
        Request request = new Request("GET", "/" + INDEX + "/_search");
        String aggregationJson = String.format("""
            {
              "size": 0,
              "aggs": {
                "%s": {
                  "%s": {
                    "field": "%s"
                  }
                }
              }
            }
            """, aggregationName, metricType, metricField);
        request.setJsonEntity(aggregationJson);

        Response response = client.performRequest(request);
        JsonNode responseBody = objectMapper.readTree(response.getEntity().getContent());
        JsonNode aggregation = responseBody.path("aggregations").path(aggregationName);

        return Map.of("result", aggregation);
    }
}
