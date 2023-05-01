package com.example.medicallab;

public class Doctor {
    private int id;
    private String name;
    private String specialty;
    private String email;
    private double discount;

    public Doctor() {}

    public Doctor(int id, String name, String speciality, String email, double discount) {
        this.id = id;
        this.name = Main.capitalize(name);
        this.specialty = Main.capitalize(speciality);
        this.email = email;
        this.discount = discount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
