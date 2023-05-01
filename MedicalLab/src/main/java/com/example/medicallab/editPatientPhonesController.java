package com.example.medicallab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class editPatientPhonesController implements Initializable {
    @FXML
    private ListView<String> listView;

    private final HashMap<String, String> updatedFields = new HashMap<>();
    private final HashMap<String, String> primaryKey = new HashMap<>();
    private String oldValue;
    private Patient patient;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        patient = PatientController.getPatient();
        primaryKey.put("patient_id", String.valueOf(patient.getPatient_id()));
        listView.getItems().addAll(patient.getPhone_numbers());
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditStart(event -> oldValue = listView.getSelectionModel().getSelectedItem());
        listView.setOnEditCommit(event2 -> {
            String newValue = event2.getNewValue();

            if (newValue.matches("\\d+")) {
                if (oldValue != null && !oldValue.isEmpty() && !oldValue.equals(newValue)) {
                    updatedFields.put("phone_number", newValue);
                    listView.getItems().set(event2.getIndex(), newValue);
                    try {
                        primaryKey.put("phone_number", oldValue);
                        Main.updateRecord("Patient_phones", primaryKey, updatedFields);
                        int index = patient.getPhone_numbers().indexOf(oldValue);
                        patient.getPhone_numbers().set(index, newValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        primaryKey.clear();
                    }
                }
                else {
                    patient.getPhone_numbers().add(newValue);
                    try {
                        Main.addPatientPhone(newValue, patient.getPatient_id());
                        listView.getItems().set(event2.getIndex(), event2.getNewValue());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                System.out.println(oldValue);
                listView.getItems().removeIf(s -> s.matches("\\D*") && oldValue.isEmpty());
            }
        });
    }

    @FXML
    private void closePage(ActionEvent e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void removePhone(ActionEvent e) {
        String val = listView.getSelectionModel().getSelectedItem();
        primaryKey.put("phone_number", val);
        try {
            Main.deleteRecord("Patient_phones", primaryKey);
            patient.getPhone_numbers().remove(val);
            listView.getItems().remove(val);
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            primaryKey.clear();
        }
    }

    @FXML
    private void addAnotherPhone(ActionEvent e) {
        listView.getItems().add("");
        int index = listView.getItems().size()-1;
        listView.getSelectionModel().select(index);

        listView.getFocusModel().focus(index);
        listView.edit(index);
    }
}
