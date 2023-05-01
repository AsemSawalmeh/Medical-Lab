package com.example.medicallab;

import java.time.LocalDate;
import java.util.ArrayList;

public class Employee {
    private int id;
    private String shift;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private LocalDate hireDate; // hire date is the current date, no need for input from user
    private String jobDesc;
    private int branchId;
    private double salary;
    private ArrayList<String> phones;

    public Employee(){}

    public Employee(int id, String shift, String name, LocalDate birthDate, String gender, String address
            , LocalDate hireDate, String jobDesc, int branchId, double salary) {
        this.id = id;
        this.shift = shift;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.hireDate = hireDate;
        this.jobDesc = jobDesc;
        this.branchId = branchId;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public ArrayList<String> getPhones() {
        return phones;
    }

    public void setPhones(ArrayList<String> phones) {
        this.phones = phones;
    }
}
