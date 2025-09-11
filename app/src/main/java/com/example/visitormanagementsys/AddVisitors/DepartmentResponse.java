package com.example.visitormanagementsys.AddVisitors;

import java.util.List;

public class DepartmentResponse {
    private boolean status;
    private List<Department> departments;

    public boolean isStatus() {
        return status;
    }

    public List<Department> getDepartments() {
        return departments;
    }
}
