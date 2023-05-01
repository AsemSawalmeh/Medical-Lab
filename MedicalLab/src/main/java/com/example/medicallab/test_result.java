package com.example.medicallab;

public class test_result {
    private int vid;
    private int TSN;
    private String result;

    public test_result(int vid, int TSN, String result) {
        this.vid = vid;
        this.TSN = TSN;
        this.result = result;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getTSN() {
        return TSN;
    }

    public void setTSN(int TSN) {
        this.TSN = TSN;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
