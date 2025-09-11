package com.example.visitormanagementsys.AddVisitors;

public class AddVisitorModel {
    private String name;
    private String mobile;
    private String address;
    private String company;
    private String purpose;
    private String department;
    private String employee;
    private String photo;

    public AddVisitorModel(String name, String mobile, String address, String company, String purpose, String department, String employee, String photo) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.company = company;
        this.purpose = purpose;
        this.department = department;
        this.employee = employee;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
}
