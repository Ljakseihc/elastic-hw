package com.epam.nosql.search.controller;

import com.epam.nosql.search.dto.Employee;
import com.epam.nosql.search.dto.QueryType;
import com.epam.nosql.search.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() throws Exception {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PostMapping
    public ResponseEntity<String> createEmployee(@RequestParam String id, @RequestBody Employee employee) throws Exception {
        return ResponseEntity.ok(employeeService.createEmployee(id, employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String id) throws Exception {
        return ResponseEntity.ok(employeeService.deleteEmployee(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployee(@RequestParam String field, @RequestParam String value, @RequestParam QueryType queryType) throws Exception {
        return ResponseEntity.ok(employeeService.searchEmployees(queryType, field, value));
    }

    @GetMapping("/aggregate")
    public ResponseEntity<Map<String, Object>> performAggregation(
            @RequestParam String aggregationName,
            @RequestParam String metricType,
            @RequestParam String metricField
    ) throws Exception {
        return ResponseEntity.ok(employeeService.performAggregation(aggregationName, metricType, metricField));
    }

}
