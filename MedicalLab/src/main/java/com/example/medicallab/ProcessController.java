package com.example.medicallab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.example.medicallab.Main.setProcessed;
import static com.example.medicallab.Main.updateTestResults;
import static com.example.medicallab.VisitController.toBeProcessVisitId;

public class ProcessController {

    @FXML
    private TableView<test_result> processTable;

    @FXML
    private TableColumn<test_result,Integer> vidColumn;

    @FXML
    private TableColumn<test_result,Integer> tsnColumn;

    @FXML
    private TableColumn<test_result, TextArea> resultArea;

    @FXML
    private Button processButton;

    private final ObservableList<test_result> visitTestsList= FXCollections.observableArrayList();

    private final ArrayList<String> resultList=new ArrayList<>(100);

    public void initialize() throws SQLException {
        int process_id=toBeProcessVisitId;
        resultList.clear();
        vidColumn.setCellValueFactory(new PropertyValueFactory<>("vid"));
        tsnColumn.setCellValueFactory(new PropertyValueFactory<>("TSN"));

        Main.loadVisitTests(visitTestsList,process_id);
        processTable.setItems(visitTestsList);

        resultArea.setCellFactory(col -> {
            TextField resultField = new TextField();

            return new TableCell<>() {

                @Override
                protected void updateItem(TextArea item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(resultField);

                        resultField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            public void handle(KeyEvent keyEvent) {
                                if (keyEvent.getCode() == KeyCode.ENTER) {
                                    resultList.add(resultField.getText());
                                    resultField.setEditable(false);
                                }
                            }
                        });

                    }
                }
            };
        });
    }

    @FXML
    public void processVisit(ActionEvent e) throws SQLException {
        for(int i=0;i<visitTestsList.size();i++) {
            System.out.println();
            visitTestsList.get(i).setResult(resultList.get(i));
        }
        int vid=toBeProcessVisitId;
        setProcessed(vid);
        updateTestResults(visitTestsList,vid);

        visitTestsList.clear();
        resultList.clear();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void iMadeAMistake(){
        resultList.clear();
        resultArea.setEditable(true);
    };
}
