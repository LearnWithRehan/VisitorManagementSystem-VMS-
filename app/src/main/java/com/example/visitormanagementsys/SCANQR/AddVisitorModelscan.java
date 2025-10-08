package com.example.visitormanagementsys.SCANQR;

public class AddVisitorModelscan {
    private String name, eamil, address, company, purpose, department, employee, photo, employeeEmail;

    public AddVisitorModelscan(String name, String eamil, String address, String company, String purpose, String department, String employee, String photo, String employeeEmail) {
        this.name = name;
        this.eamil = eamil;
        this.address = address;
        this.company = company;
        this.purpose = purpose;
        this.department = department;
        this.employee = employee;
        this.photo = photo;
        this.employeeEmail = employeeEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEamil() {
        return eamil;
    }

    public void setEamil(String eamil) {
        this.eamil = eamil;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
}
