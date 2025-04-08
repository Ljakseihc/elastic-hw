package com.epam.nosql.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.epam.nosql.search.dto.Employee;
import com.epam.nosql.search.dto.QueryType;
import com.epam.nosql.search.dto.StatType;
import com.epam.nosql.search.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final ElasticsearchClient esClient;
    private static final String INDEX = "employees";

    @Autowired
    public EmployeeServiceImpl(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public Employee getEmployeeById(String id) throws IOException {
        GetResponse<Employee> response = esClient.get(g -> g
                        .index(INDEX)
                        .id(id),
                Employee.class
        );
        return response.source();
    }

    @Override
    public List<Employee> getAllEmployees() throws IOException {
        SearchResponse<Employee> search = esClient.search(s -> s
                        .index(INDEX),
                Employee.class);
        return search.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    @Override
    public String createEmployee(String id, Employee employee) throws IOException {
        esClient.index(d -> d.index(INDEX).id(id).document(employee));
        return "Created employee with ID " + id;
    }

    @Override
    public String deleteEmployee(String id) throws IOException {
        esClient.delete(d -> d.index(INDEX).id(id));
        return "Deleted employee with ID " + id;
    }

    @Override
    public List<Employee> searchEmployees(QueryType queryType, String field, String value) throws IOException {
        SearchResponse<Employee> response = esClient.search(s -> s
                        .index(INDEX)
                        .query(q -> switch (queryType) {
                                    case match -> q.match(t -> t
                                            .field(field)
                                            .query(value)
                                    );
                                    case term -> q.term(t -> t
                                            .field(field)
                                            .value(value)
                                    );
                                }
                        ),
                Employee.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }

    @Override
    public Map<String, Object> performAggregation(String aggregationName, StatType metricType, String metricField) throws IOException {
        SearchResponse<Void> response = esClient.search(b -> b
                        .index(INDEX)
                        .size(0)
                        .aggregations(aggregationName, a -> switch (metricType) {
                            case avg -> a.avg(t -> t.field(metricField));
                            case sum -> a.sum(t -> t.field(metricField));
                            case min -> a.min(t -> t.field(metricField));
                            case max -> a.max(t -> t.field(metricField));
                        }),
                Void.class
        );

        return switch (metricType) {
            case avg -> Map.of(StatType.avg.toString(), response.aggregations().get(aggregationName).avg().value());
            case sum -> Map.of(StatType.sum.toString(), response.aggregations().get(aggregationName).sum().value());
            case min -> Map.of(StatType.min.toString(), response.aggregations().get(aggregationName).min().value());
            case max -> Map.of(StatType.max.toString(), response.aggregations().get(aggregationName).max().value());
        };
    }
}
