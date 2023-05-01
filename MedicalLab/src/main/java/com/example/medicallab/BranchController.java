package com.example.medicallab;

import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class BranchController implements Initializable {
    private Branch branch;
    private final HashMap<String, String> primaryKey = new HashMap<>();
    private final HashMap<String, String> alteredFields = new HashMap<>();

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField searchBox;
    @FXML
    private TableView<Branch> tableView;
    @FXML
    private TableColumn<Branch, Integer> idColumn;
    @FXML
    private TableColumn<Branch, String> nameColumn;
    @FXML
    private TableColumn<Branch, String> locationColumn;
    @FXML
    private TableColumn<Branch, String> workingHoursColumn;
    @FXML
    private TableColumn<Branch, HBox> EditRemoveColumn;


    //add branch
    @FXML
    private TextField BranchName;
    @FXML
    private TextField workingHours;
    @FXML
    private TextField BranchLocation;
    @FXML
    private TitledPane addBranchPane;

    //edit branch
    @FXML
    private AnchorPane editPane;
    @FXML
    private TextField editName;
    @FXML
    private TextField editLocatoin;
    @FXML
    private TextField editWorkingHours;
    private final ObservableList<Branch> dataList = FXCollections.observableArrayList();


    public void initialize(URL url, ResourceBundle rb) {
/*        try {
            Main.loadAllBranches(dataList);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }*/
        FilteredList<Branch> filteredData = new FilteredList<>(dataList);
        searchBox.setOnKeyTyped(e -> filteredData.setPredicate(br -> {
            if (searchBox.getText().isEmpty())
                return true;

            String lowerCaseFilter = searchBox.getText().toLowerCase();

            if (br.getBranchName().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (String.valueOf(br.getBid()).startsWith(lowerCaseFilter))
                return true;
            else
                return false;
        }));
        SortedList<Branch> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        addBranchPane.setExpanded(false);
        editPane.setVisible(false);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bid"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        workingHoursColumn.setCellValueFactory(new PropertyValueFactory<>("workingHours"));

        EditRemoveColumn.setCellFactory(col -> {
            Hyperlink edit = new Hyperlink("Edit");
            Hyperlink delete = new Hyperlink("Delete");

            edit.setOnAction(e -> {
            });

            return new TableCell<>() {
                @Override
                protected void updateItem(HBox item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty)
                        setGraphic(null);
                    else {
                        HBox box = new HBox(10);
                        box.getChildren().addAll(edit, delete);
                        setGraphic(box);
                        delete.setOnAction(e -> {
                            branch = getTableView().getItems().get(getIndex());
                            primaryKey.put("Branch_id", String.valueOf(branch.getBid()));
                            /*try {
                                Main.deleteRecord("Branch", primaryKey);
                                dataList.remove(branch);
                            } catch (SQLException exp) {
                                exp.printStackTrace();
                            } finally {
                                primaryKey.clear();
                            }*/
                        });
                        edit.setOnAction(e -> {
                            root.setPrefWidth(1840);
                            editPane.setVisible(true);
                            branch = getTableView().getItems().get(getIndex());
                            fillBranchData();
                            setAllFieldsEditable(true);

                            Transition transition = new Transition() {
                                {
                                    setCycleDuration(Duration.millis(300));
                                }

                                @Override
                                protected void interpolate(double v) {
                                    scrollPane.setHvalue(v);
                                }
                            };
                            transition.play();
                            transition.setOnFinished(e2 -> scrollPane.setPadding(new Insets(0, 0, 0, -921)));
                        });
                    }
                }
            };
        });

    }

    @FXML
    private void expandPane(MouseEvent e) {
        if (addBranchPane.isExpanded() && scrollPane.getVvalue() < 0.5) {
            root.setPrefHeight(1100);
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.millis(300));
                }
                protected void interpolate(double frac) {
                    scrollPane.setVvalue(frac);
                }
            };
            transition.play();
        }
        else if (!addBranchPane.isExpanded()){
            Transition transition = new Transition() {
                {
                    setCycleDuration(Duration.millis(350));
                }
                protected void interpolate(double frac) {
                    scrollPane.setVvalue(1 - frac);
                }
            };
            transition.play();
            transition.setOnFinished(event2 -> root.setPrefHeight(630));
        }
    }

    @FXML
    private void add_closeBranchButton(ActionEvent e) {
        addBranchPane.setExpanded(!addBranchPane.isExpanded());
        MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, false, false, false, false, false, false, null);
        addBranchPane.fireEvent(mouseEvent);
        BranchName.clear();
        BranchLocation.clear();
        workingHours.clear();
        addBranchPane.setPrefHeight(470);
    }

    @FXML
    private void cancelEdit(ActionEvent e) {
        scrollPane.setPadding(new Insets(0, 0, 0, 0));

        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(300));
            }
            @Override
            protected void interpolate(double v) {
                scrollPane.setHvalue(1 - v);
            }
        };
        transition.play();
        transition.setOnFinished(e2 -> {
            root.setPrefWidth(920);
            editPane.setVisible(false);
        });
    }

    @FXML
    private void updateBranch(ActionEvent e) {
        String name = editName.getText();
        String location = editLocatoin.getText();
        String workHou = editWorkingHours.getText();

        primaryKey.put("branch_id", String.valueOf(branch.getBid()));

        if (!branch.getBranchName().equalsIgnoreCase(name)) {
            alteredFields.put("branch_name", "'" + name + "'");
            branch.setBranchName(Main.capitalize(name));
        }
        if (!branch.getLocation().equalsIgnoreCase(location)) {
            alteredFields.put("location", "'" + location + "'");
            branch.setLocation(location);
        }
        if (branch.getWorkingHours().equalsIgnoreCase(workHou)) {
            alteredFields.put("working_hours", workHou);
            branch.setWorkingHours(workHou);
        }
        try {
            Main.updateRecord("Branch", primaryKey, alteredFields);
            tableView.refresh();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            alteredFields.clear();
        }
    }

    private void fillBranchData() {
        primaryKey.put("Branch_id", String.valueOf(branch.getBid()));
        editName.setText(branch.getBranchName());
        editLocatoin.setText(branch.getLocation());
        editWorkingHours.setText(String.valueOf(branch.getWorkingHours()));

    }

    private void setAllFieldsEditable(boolean editable) {
        editName.setEditable(editable);
        editWorkingHours.setEditable(editable);
        editLocatoin.setEditable(editable);
    }
}