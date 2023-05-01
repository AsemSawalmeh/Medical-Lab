package com.example.medicallab;

public class medicalTest {
    private int tsn;
    private String test_name;
    private String range;
    private double charge_price;
    private int process_time;

    public medicalTest(int tsn, String test_name, String range, int charge_price, int process_time) {
        this.tsn = tsn;
        this.test_name = test_name;
        this.range = range;
        this.charge_price = charge_price;
        this.process_time = process_time;
    }

    public int getTsn() {
        return tsn;
    }

    public void setTsn(int tsn) {
        this.tsn = tsn;
    }

    public  String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name = test_name;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public double getCharge_price() {
        return charge_price;
    }

    public void setCharge_price(int charge_price) {
        this.charge_price = charge_price;
    }

    public int getProcess_time() {
        return process_time;
    }

    public void setProcess_time(int process_time) {
        this.process_time = process_time;
    }
}
