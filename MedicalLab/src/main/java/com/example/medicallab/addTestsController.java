package com.example.medicallab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;

import static com.example.medicallab.Main.loadAllTests;
import static com.example.medicallab.VisitController.toBeProcessVisitId;

public class addTestsController {

    @FXML
    private TextField searchbar;
    @FXML
    private Button doneButton;

    @FXML
    private TableView<medicalTest> avtTable;

    @FXML
    private TableColumn<medicalTest,Integer> atsn;

    @FXML
    private TableColumn<medicalTest,String> atest_name;

    @FXML
    private TableColumn<medicalTest,String> aresult_range;

    @FXML
    private TableColumn<medicalTest,Integer> acharge_price;

    @FXML
    private TableColumn<medicalTest,Integer> aprocess_time;

    @FXML
    private TableView<medicalTest> incTable;

    @FXML
    private TableColumn<medicalTest,Integer> inctsn;

    @FXML
    private TableColumn<medicalTest,String> inctest_name;

    @FXML
    private TableColumn<medicalTest,String> incresult_range;

    @FXML
    private TableColumn<medicalTest,Integer> inccharge_price;

    @FXML
    private TableColumn<medicalTest,Integer> incprocess_time;

    private final ObservableList<medicalTest> avtestlist= FXCollections.observableArrayList();

    private final ObservableList<medicalTest> inctestlist= FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        atsn.setCellValueFactory(new PropertyValueFactory<>("tsn"));
        atest_name.setCellValueFactory(new PropertyValueFactory<>("test_name"));
        aresult_range.setCellValueFactory(new PropertyValueFactory<>("range"));
        acharge_price.setCellValueFactory(new PropertyValueFactory<>("charge_price"));
        aprocess_time.setCellValueFactory(new PropertyValueFactory<>("process_time"));

        inctsn.setCellValueFactory(new PropertyValueFactory<>("tsn"));
        inctest_name.setCellValueFactory(new PropertyValueFactory<>("test_name"));
        incresult_range.setCellValueFactory(new PropertyValueFactory<>("range"));
        inccharge_price.setCellValueFactory(new PropertyValueFactory<>("charge_price"));
        incprocess_time.setCellValueFactory(new PropertyValueFactory<>("process_time"));

        loadAllTests(avtestlist);
        avtTable.setItems(avtestlist);


        FilteredList<medicalTest> filteredData = new FilteredList<>(avtestlist);
        searchbar.setOnKeyTyped(e -> filteredData.setPredicate(patient -> {
            if (searchbar.getText().isEmpty())
                return true;

            String lowerCaseFilter = searchbar.getText().toLowerCase();

            /*if (medicalTest.getTest_name().toLowerCase().contains(lowerCaseFilter))
                return true;*/

            return false;
        }));
    };

    @FXML
    private void a2inc(MouseEvent e){
        inctestlist.add(avtTable.getSelectionModel().getSelectedItem());
        avtestlist.remove(avtTable.getSelectionModel().getSelectedItem());
        incTable.setItems(inctestlist);
    };
    @FXML
    private void inc2a(MouseEvent e){
        avtestlist.add(incTable.getSelectionModel().getSelectedItem());
        inctestlist.remove(incTable.getSelectionModel().getSelectedItem());
        avtTable.setItems(avtestlist);
    };

    @FXML
    private void closeTestsWindow(ActionEvent e) throws SQLException {
        for(int i=0;i<inctestlist.size();i++) {
            VisitController.priceSum+=inctestlist.get(i).getCharge_price();
            VisitController.testAdditionList.add(inctestlist.get(i));
        }
        toBeProcessVisitId=0;
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    };
}
