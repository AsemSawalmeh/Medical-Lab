package com.example.medicallab;

public class Branch {
    private String workingHours;
    private String location;
    private int bid;
    private String branchName;

    public Branch(){

    }

    public Branch(String workingHours, String location, int bid, String branchName) {
        this.workingHours = workingHours;
        this.location = location;
        this.bid = bid;
        this.branchName = branchName;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}