package com.example.medicallab;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Visit{

    private int visit_id;
    private String payment_method;
    private double charge_price;
    private int branch_id;
    private int patient_id;
    private int doctor_id;
    private int emp_id;
    private boolean process_status;
    private LocalDateTime date_time;

    private ArrayList<medicalTest> tests;

    public Visit(int visit_id, String payment_method, int charge_price, int branch_id, int patient_id, int doctor_id, int emp_id, boolean process_status, LocalDateTime date_time) {
        this.visit_id = visit_id;
        this.payment_method = payment_method;
        this.charge_price = charge_price;
        this.branch_id = branch_id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.emp_id = emp_id;
        this.process_status = process_status;
        this.date_time = date_time;
    }

    public int getVisit_id() {return visit_id;}

    public void setVisit_id(int visit_id) {
        this.visit_id = visit_id;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public double getCharge_price() {
        return charge_price;
    }

    public void setCharge_price(int charge_price) {
        this.charge_price = charge_price;
    }

    public int getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(int branch_id) {
        this.branch_id = branch_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(int emp_id) {
        this.emp_id = emp_id;
    }

    public boolean isProcess_status() {
        return process_status;
    }

    public void setProcess_status(boolean process_status) {
        this.process_status = process_status;
    }

    public LocalDateTime getDate_time() {
        return date_time;
    }

    public void setDate_time(LocalDateTime date_time) {
        this.date_time = date_time;
    }

    @Override
    public String toString() {
        return
                "" + visit_id +
                " " + payment_method +
                " " + charge_price +
                " " + branch_id +
                " " + patient_id +
                " " + doctor_id +
                " " + emp_id +
                " " + process_status +
                " " + date_time +
                " " + tests +
                "";
    }
}

