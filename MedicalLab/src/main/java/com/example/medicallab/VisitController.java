package com.example.medicallab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.medicallab.Main.*;

public class VisitController {

    @FXML
    private TableView<Visit> vtable;

    @FXML
    private TableColumn<Visit,Integer> vidcolumn;

    @FXML
    private TableColumn<Visit,Integer> bidcolumn;

    @FXML
    private TableColumn<Visit,Integer> pidcolumn;

    @FXML
    private TableColumn<Visit,Integer> eidcolumn;

    @FXML
    private TableColumn<Visit,Integer> didcolumn;

    @FXML
    private TableColumn<Visit, LocalDateTime> datetimecolumn;

    @FXML
    private TableColumn<Visit,Boolean> processstatus;

    @FXML
    private TableColumn<Visit,String> paymethod;

    @FXML
    private TableColumn<Visit,Double> chargeprice;
    @FXML
    private Button addTestsButton;

    @FXML
    private Button addVisitButton;

    @FXML
    private AnchorPane testsWindow;

    @FXML
    private TextField patientText;

    @FXML
    private TextField empText;

    @FXML
    private TextField branchText;

    @FXML
    private TextField doctorText;

    @FXML
    private RadioButton cashRad;

    @FXML
    private RadioButton cardRad;

    @FXML
    private ToggleGroup payGrp;

    @FXML
    private TableColumn<Visit, Hyperlink> deleteColumn;

    @FXML
    private TableColumn<Visit, Hyperlink> processColumn;

    @FXML
    private TableColumn<Visit, Hyperlink> receiptColumn;

    private String payMthd;
    private final ObservableList<Visit> vList = FXCollections.observableArrayList();

    public static double priceSum=0.0;

    public static int toBeProcessVisitId=0;
    public static List<medicalTest> testAdditionList=new ArrayList<>(50);

    public void initialize() throws SQLException {
        vidcolumn.setCellValueFactory(new PropertyValueFactory<>("visit_id"));
        bidcolumn.setCellValueFactory(new PropertyValueFactory<>("branch_id"));
        pidcolumn.setCellValueFactory(new PropertyValueFactory<>("patient_id"));
        eidcolumn.setCellValueFactory(new PropertyValueFactory<>("emp_id"));
        didcolumn.setCellValueFactory(new PropertyValueFactory<>("doctor_id"));
        datetimecolumn.setCellValueFactory(new PropertyValueFactory<>("date_time"));
        processstatus.setCellValueFactory(new PropertyValueFactory<>("process_status"));
        chargeprice.setCellValueFactory(new PropertyValueFactory<>("charge_price"));
        paymethod.setCellValueFactory(new PropertyValueFactory<>("payment_method"));


        loadAllVisits(vList);
        vtable.setItems(vList);

        deleteColumn.setCellFactory(col -> {
            Hyperlink deletevisitlink = new Hyperlink("Delete");
            deletevisitlink.setStyle("-fx-text-fill:#ff794d;");

            return new TableCell<>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deletevisitlink);
                        deletevisitlink.setOnMouseClicked(e -> {
                            Visit v = getTableView().getItems().get(getIndex());
                            Main.deleteVisit(vList, "Visits", v);
                        });
                    }
                }
            };

        });

        processColumn.setCellFactory(col -> {
            Hyperlink processvisitlink = new Hyperlink("Process");
            processvisitlink.setStyle("-fx-text-fill:#ff794d;");

            return new TableCell<>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(processvisitlink);
                        processvisitlink.setOnMouseClicked(e -> {
                            vtable.getSelectionModel().getSelectedItem().setProcess_status(true);
                            toBeProcessVisitId=vtable.getSelectionModel().getSelectedItem().getVisit_id();
                            try {
                                openProcessWindow();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }
            };

        });

        receiptColumn.setCellFactory(col -> {
            Hyperlink receiptLink = new Hyperlink("Receipt");
            receiptLink.setStyle("-fx-text-fill:#ff794d;");

            return new TableCell<>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(receiptLink);
                        receiptLink.setOnMouseClicked(e -> {
                            toBeProcessVisitId=vtable.getSelectionModel().getSelectedItem().getVisit_id();
                            List<String> writtenList=new ArrayList<>(50);
                            List<String> imageList=new ArrayList<>(1);
                            imageList.add("C:\\Users\\Asem\\Desktop\\Uni\\Year 3\\1\\Data Base\\Project\\Lab.png");
                            writtenList.add("Visit ID: "+vtable.getSelectionModel().getSelectedItem().getVisit_id());
                            try {
                                writtenList.add("Patient Name: "+Main.getPatientName(vtable.getSelectionModel().getSelectedItem().getPatient_id()));
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            try {
                                writtenList.add("Branch: "+Main.getBranchName(vtable.getSelectionModel().getSelectedItem().getBranch_id()));
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            writtenList.add("Date_Time: "+vtable.getSelectionModel().getSelectedItem().getDate_time());
                            writtenList.add("Charge Price: "+vtable.getSelectionModel().getSelectedItem().getCharge_price()+"$");
                            try {
                                writtenList.add("Employee Name: "+getEmployeeName(vtable.getSelectionModel().getSelectedItem().getEmp_id()));
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            writtenList.add("\n\n");

                            writtenList.add("TEST NAME   Normal Range   Result");

                            writtenList.add("\n");

                            try {
                                List<String> rawList=Main.getTestResults(vtable.getSelectionModel().getSelectedItem().getVisit_id());

                                for(int i=0;i<rawList.size();i++){
                                    String[] s1=rawList.get(i).split(" ");
                                    Integer ssn=Integer.parseInt(s1[0]);
                                    String added=getTestNR(ssn);
                                    String res=s1[1];
                                    writtenList.add(added+" "+res);
                                    writtenList.add("\n");
                                }

                                generatePDF(writtenList,imageList,"Lab.png"+toBeProcessVisitId+".pdf");
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }
            };

        });
    };

    @FXML
    public void insertVisitJV() throws SQLException {
        int id=0;
        String payment_method = payMthd;
        double chargeprice;
        int did;

        if(doctorText.getText().isEmpty()){
            chargeprice=priceSum;
            did=0;
        }
        else
        {
            chargeprice = priceSum*getDoctorDiscount(Integer.parseInt(doctorText.getText()));
            did = Integer.parseInt(doctorText.getText());
        }

        if(hasDiscount(Integer.parseInt(patientText.getText()))){
            chargeprice=chargeprice*0.95;
        }


        int bid = Integer.parseInt(branchText.getText());
        int pid = Integer.parseInt(patientText.getText());
        int eid = Integer.parseInt(empText.getText());
        boolean processstatus=false;
        LocalDateTime date = LocalDateTime.now();
        if(doctorText.getText().isEmpty()){Main.addVisitNoDoc(
                vList,id, payment_method, chargeprice, bid, pid,eid,processstatus,date);
        }
        else {
            Main.addVisit(vList,id, payment_method, chargeprice, bid, pid, did, eid,processstatus,date);
        }

        priceSum= 0.0;
        vList.clear();
        loadAllVisits(vList);
        id=Main.selectLastVisitInt();
        addTestResult(testAdditionList,id);

        testAdditionList.clear();
        patientText.setText("");
        branchText.setText("");
        empText.setText("");
        doctorText.setText("");
        payMthd="";

    };

    @FXML
    private void openTestsWindow(ActionEvent e) throws IOException, SQLException {
        toBeProcessVisitId=Main.selectLastVisitInt()+1;
        AnchorPane newRoot = FXMLLoader.load(getClass().getResource("addTestsWindow.fxml"));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(newRoot));
        newStage.show();
    };

    private void openProcessWindow() throws IOException{
        AnchorPane newRoot = FXMLLoader.load(getClass().getResource("ProcessTests.fxml"));
        Stage newStage = new Stage();
        newStage.setScene(new Scene(newRoot));
        newStage.show();
    }

    @FXML
    private void setCash(){payMthd="Cash";}

    @FXML
    private void setCard(){payMthd="Card";}

}
