package com.example.medicallab;

import java.time.LocalDate;
import java.util.ArrayList;

public class Patient {
    private int patient_id;
    private LocalDate date_of_birth;
    private String patient_name;
    private String gender;
    private String address;
    private String email;
    private boolean hasInsurance;
    private ArrayList<String> phone_numbers;

    public Patient(){}

    public Patient(int patient_id, LocalDate date_of_birth, String patient_name, String gender, String address
            , String email, boolean hasInsurance) {
        this.patient_id = patient_id;
        this.date_of_birth = date_of_birth;
        this.patient_name = Main.capitalize(patient_name);
        this.gender = Main.capitalize(gender);
        this.address = Main.capitalize(address);
        this.email = email;
        this.hasInsurance = hasInsurance;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = Main.capitalize(patient_name);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = Main.capitalize(gender);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = Main.capitalize(address);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getPhone_numbers() {
        return this.phone_numbers;
    }

    public void setPhone_numbers(ArrayList<String> phone_numbers) {
        this.phone_numbers = phone_numbers;
    }

    public boolean getHasInsurance() {
        return hasInsurance;
    }

    public void setHasInsurance(boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    @Override
    public String toString() {
        return patient_id + "\t" + patient_name + "\t" + gender + "\t" + date_of_birth + "\t" + address + "\t" + email;
    }
}
