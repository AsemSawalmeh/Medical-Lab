package com.example.medicallab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

import static com.example.medicallab.Main.loadAllTests;

public class testsController {

    @FXML
    private TableView<medicalTest> testTable;

    @FXML
    private TableColumn<medicalTest,Integer> tsn;

    @FXML
    private TableColumn<medicalTest,String> test_name;

    @FXML
    private TableColumn<medicalTest,String> result_range;

    @FXML
    private TableColumn<medicalTest,Integer> charge_price;

    @FXML
    private TableColumn<medicalTest,Integer> process_time;

    @FXML
    private TableColumn<medicalTest, Hyperlink> deleteLink;

    @FXML
    private TextField nameField;

    @FXML
    private TextField resultField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField pTimeField;

    @FXML
    private Button addTestButton;

    private final ObservableList<medicalTest> testlist= FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        tsn.setCellValueFactory(new PropertyValueFactory<>("tsn"));
        test_name.setCellValueFactory(new PropertyValueFactory<>("test_name"));
        result_range.setCellValueFactory(new PropertyValueFactory<>("range"));
        charge_price.setCellValueFactory(new PropertyValueFactory<>("charge_price"));
        process_time.setCellValueFactory(new PropertyValueFactory<>("process_time"));

        loadAllTests(testlist);
        testTable.setItems(testlist);

        deleteLink.setCellFactory(col -> {
            Hyperlink deletetestlink = new Hyperlink("Delete");
            deletetestlink.setStyle("-fx-text-fill:#ff794d;");

            return new TableCell<>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deletetestlink);
                        deletetestlink.setOnMouseClicked(e -> {
                            medicalTest mt = getTableView().getItems().get(getIndex());
                            Main.deleteTest(testlist, "medical_tests", mt);
                        });
                    }
                }
            };

        });
    }

    @FXML
    public void insertTestJV() throws SQLException {
        int id=0;
        String testName = nameField.getText();
        String resultRange=resultField.getText();
        int chargeprice=Integer.parseInt(priceField.getText());
        int ptime=Integer.parseInt(pTimeField.getText());

        Main.addTest(testlist,id,testName,resultRange,chargeprice,ptime);

        testlist.clear();
        loadAllTests(testlist);


        nameField.setText("");
        resultField.setText("");
        priceField.setText("");
        pTimeField.setText("");
    };
}
