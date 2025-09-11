package com.example.visitormanagementsys.AddVisitors;

import java.util.List;

public class EmployeeResponse {
    private boolean status;
    private List<Employee> employees;

    public boolean isStatus() {
        return status;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}
