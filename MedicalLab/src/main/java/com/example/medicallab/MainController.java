package com.example.medicallab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
public class MainController implements Initializable {
    @FXML
    private Text numOfPatients;
    @FXML
    private Text numOfEmp;
    @FXML
    private Text totalCash;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private Text totalIncomeText;
    @FXML
    private Label topBranchName;
    @FXML
    private Label topBranchVisits;
    @FXML
    private Text firstEmployee;
    @FXML
    private Label firstEmpVisits;
    @FXML
    private Text secEmployee;
    @FXML
    private Label secEmpVisits;
    @FXML
    private Text thirdEmployee;
    @FXML
    private Label thirdEmpVisits;
    @FXML
    private Text firstTest;
    @FXML
    private Label firstTestCount;
    @FXML
    private Text secTest;
    @FXML
    private Label secTestCount;
    @FXML
    private Text thirdTest;
    @FXML
    private Label thirdTestCount;
    @FXML
    private Text fourthTest;
    @FXML
    private Label fourthTestCount;
    @FXML
    private Text fifthTest;
    @FXML
    private Label fifthTestCount;

    @FXML
    private ChoiceBox patientQryBox;

    @FXML
    private TextField patientText1;

    @FXML
    private ChoiceBox visitQryBox;

    @FXML
    private TextField visitText1;

    @FXML
    private ChoiceBox testQueryBox;

    @FXML
    private TextField testText1;

    @FXML
    private ChoiceBox branchQueryBox;

    @FXML
    private TextField branchText1;

    @FXML
    private ChoiceBox employeeQueryBox;

    @FXML
    private TextField employeeText1;

    @FXML
    private ChoiceBox availableTests;

    @FXML
    private TextField testText2;

    @FXML
    private ChoiceBox availableBranches;

    @FXML
    private TextField branchText2;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        endDate.setValue(LocalDate.now());
        startDate.setValue(LocalDate.now().minus(Period.ofDays(1)));
        firstEmployee.setVisible(false);
        firstEmpVisits.setVisible(false);
        secEmployee.setVisible(false);
        secEmpVisits.setVisible(false);
        thirdEmployee.setVisible(false);
        thirdEmpVisits.setVisible(false);
        try {
            fillAllLabels();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        patientText1.setEditable(false);
        visitText1.setEditable(false);
        availableTests.setVisible(false);
        testText2.setVisible(false);
        availableBranches.setVisible(false);
        branchText2.setVisible(false);

        patientQryBox.getItems().addAll("Percentage Of Female Patients","Percentage Of Male Patients");
        visitQryBox.getItems().addAll("Average Charge Price","Total Number of Visits");
        testQueryBox.getItems().addAll("Average Result Of Test","Percentage of Unused Tests");
        branchQueryBox.getItems().addAll("Total Earning","Number Of Employees","Number of Visits");


        patientQryBox.setOnAction(event-> {
            if(patientQryBox.getSelectionModel().getSelectedItem().equals("Percentage Of Male Patients")) {
                double perc= 0;
                try {
                    perc = Main.percOfMalePatients();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                patientText1.setText(String.valueOf(perc)+"%");
            }

            else if((patientQryBox.getSelectionModel().getSelectedItem().equals("Percentage Of Female Patients"))){
                double perc= 0;
                try {
                    perc = Main.percOfFemalePatients();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                patientText1.setText(String.valueOf(perc)+"%");
            }});

        visitQryBox.setOnAction(e->{
            if(visitQryBox.getSelectionModel().getSelectedItem().equals("Average Charge Price")) {
                double avg= 0;
                try {
                    avg = Main.avgChargePrice();
                } catch (SQLException o) {
                    throw new RuntimeException(o);
                }
                visitText1.setText(String.valueOf(avg)+"$");
            }

            else if((visitQryBox.getSelectionModel().getSelectedItem().equals("Total Number of Visits"))){
                int num=0;
                try {
                    num= Main.totalNoOfVisits(0);
                } catch (SQLException s) {
                    throw new RuntimeException(s);
                }
                visitText1.setText(String.valueOf(num)+" Visits");
            }
        });

        testQueryBox.setOnAction(f->{
            if(testQueryBox.getSelectionModel().getSelectedItem().equals("Percentage of Unused Tests")) {
                availableTests.getItems().clear();
                testText2.setVisible(false);
                availableTests.setVisible(false);
                double perc= 0;
                try {
                    perc = Main.percOfUnusedTests();
                } catch (SQLException o) {
                    o.printStackTrace();
                }
                testText1.setText(String.valueOf(perc)+"%");

            }
            else if(testQueryBox.getSelectionModel().getSelectedItem().equals("Average Result Of Test"))
            {
                testText2.setVisible(true);
                availableTests.setVisible(true);
                try {
                    Main.fillTests(availableTests);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                availableTests.setOnAction(k-> {
                    int id=0;
                    try {
                        id=Main.getIdOfTest((String) availableTests.getSelectionModel().getSelectedItem());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        testText2.setText(String.valueOf(Main.avgTestResult(id)));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        branchQueryBox.setOnAction(r->{
            if(branchQueryBox.getSelectionModel().getSelectedItem().equals("Total Earning")) {
                availableBranches.setVisible(false);
                branchText2.setVisible(false);
                double perc= 0;
                try {
                    perc = Main.branchTotalCash(0);
                } catch (SQLException o) {
                    throw new RuntimeException(o);
                }
                branchText1.setText(String.valueOf(perc)+"$");
            }

            else if(branchQueryBox.getSelectionModel().getSelectedItem().equals("Number of Visits") || branchQueryBox.getSelectionModel().getSelectedItem().equals("Number Of Employees"))
            {
                branchText2.setVisible(true);
                availableBranches.setVisible(true);
                availableBranches.getItems().clear();
                try {
                    Main.fillBranches(availableBranches);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (branchQueryBox.getSelectionModel().getSelectedItem().equals("Number of Visits") ) {
                    availableBranches.setOnAction(k -> {
                        int id = 0;
                        try {
                            id = Main.getIdOfBranch((String) availableBranches.getSelectionModel().getSelectedItem());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            branchText2.setText(String.valueOf(Main.noOfVisitsInBranch(id)));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                else if(branchQueryBox.getSelectionModel().getSelectedItem().equals("Number Of Employees")) {
                    availableBranches.setOnAction(z -> {
                        int id = 0;
                        try {
                            id = Main.getIdOfBranch((String) availableBranches.getSelectionModel().getSelectedItem());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            branchText2.setText(String.valueOf(Main.noOfEmployeesInBranch(id)));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }});
                }
            }
        });


        employeeQueryBox.getItems().addAll("Average Salary","Male Percentage","Female Percentage");
        employeeQueryBox.setOnAction(t->{
            if(employeeQueryBox.getSelectionModel().getSelectedItem().equals("Average Salary")) {
                double perc= 0;
                try {
                    perc = Main.avgEmpSalary();
                } catch (SQLException o) {
                    throw new RuntimeException(o);
                }
                employeeText1.setText(String.valueOf(perc)+"$");
            }

            else if(employeeQueryBox.getSelectionModel().getSelectedItem().equals("Female Percentage"))
            {
                double perc= 0;
                try {
                    perc = Main.percOfFemaleEmployees();
                } catch (SQLException o) {
                    throw new RuntimeException(o);
                }
                employeeText1.setText(String.valueOf(perc)+"%");
            }

            else if(employeeQueryBox.getSelectionModel().getSelectedItem().equals("Male Percentage"))
            {
                double perc= 0;
                try {
                    perc = Main.percOfMaleEmployees();
                } catch (SQLException o) {
                    throw new RuntimeException(o);
                }
                employeeText1.setText(String.valueOf(perc)+"%");
            }
        });
    }

    @FXML
    private void execute(ActionEvent e) throws SQLException {
        fillAllLabels();
    }

    private void fillAllLabels() throws SQLException {
        numOfPatients.setText(String.valueOf(Main.noOfPatients()));
        numOfEmp.setText(String.valueOf(Main.noOfEmp()));
        totalCash.setText("$" + Main.branchTotalCash(0, null, null));
        LocalDate startD = startDate.getValue();
        LocalDate endD = endDate.getValue();

        totalIncomeText.setText("$" + Main.branchTotalCash(0, Date.valueOf(startD), Date.valueOf(endD)));

        startD = startDate.getValue();
        endD = endDate.getValue();
        List<String> s = Main.topBranchWithEmp(Date.valueOf(startD), Date.valueOf(endD));
        firstEmployee.setText("");
        firstEmpVisits.setText("");
        secEmployee.setText("");
        secEmpVisits.setText("");
        thirdEmployee.setText("");
        thirdEmpVisits.setText("");
        System.out.println(s);
        String[] arr;
        if (s.size() > 0) {
            arr = s.get(0).split(",");
            topBranchName.setText("Top Branch: " + arr[0]);
            topBranchVisits.setText("With: " + arr[1] + " Visits");
        }
        // first employee
        if (s.size() > 1) {
            arr = s.get(1).split(",");
            firstEmployee.setVisible(true);
            firstEmpVisits.setVisible(true);
            firstEmployee.setText(arr[0]);
            firstEmpVisits.setText("With: " + arr[1] + " Visits");
        }
        // second employee
        if (s.size() > 2) {
            arr = s.get(2).split(",");
            secEmployee.setVisible(true);
            secEmpVisits.setVisible(true);
            secEmployee.setText(arr[0]);
            secEmpVisits.setText("With: " + arr[1] + " Visits");
        }
        // third employee
        if (s.size() > 3) {
            arr = s.get(3).split(",");
            thirdEmployee.setVisible(true);
            thirdEmpVisits.setVisible(true);
            thirdEmployee.setText(arr[0]);
            thirdEmpVisits.setText("With: " + arr[1] + " Visits");
        }
        // filling the tests labels
        ArrayList<String> list = new ArrayList<>();
        Main.topTests(list, Date.valueOf(startD), Date.valueOf(endD));

        String[] temp;
        if (list.size() > 0) {
            temp = list.get(0).split(",");
            firstTest.setText(temp[0]);
            firstTestCount.setText("With: " + temp[1] + " Use");
        }
        // second test
        if (list.size() > 1) {
            temp = list.get(1).split(",");
            secTest.setText(temp[0]);
            secTestCount.setText("With: " + temp[1] + " Use");
        }
        // third test
        if (list.size() > 2) {
            temp = list.get(2).split(",");
            thirdTest.setText(temp[0]);
            thirdTestCount.setText("With: " + temp[1] + " Use");
        }
        // fourth test
        if (list.size() > 3) {
            temp = list.get(3).split(",");
            fourthTest.setText(temp[0]);
            fourthTestCount.setText("With: " + temp[1] + " Use");
        }
        // fifth test
        if (list.size() > 4) {
            temp = list.get(4).split(",");
            fifthTest.setText(temp[0]);
            fifthTestCount.setText("With: " + temp[1] + " Use");
        }
    }


    @FXML
    private void showPatientsPage(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("patients.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 980, 640);
        stage.setTitle("Patients");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private void showDoctorsPage(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("doctors.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 980, 640);
        stage.setTitle("Doctors");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private void showVisitsPage(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Visits.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1279, 443);
        stage.setTitle("Patients");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private void showBranchesPage(ActionEvent e) {
        //branchesFile.setVisible(true);
    }

    @FXML
    private void showEmployeesPage(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("employee.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 980, 640);
        stage.setTitle("Employees");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private void showTestsPage(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("medicalTests.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 982, 435);
        stage.setTitle("Tests");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setMaximized(true);
        stage.show();
    }
}
