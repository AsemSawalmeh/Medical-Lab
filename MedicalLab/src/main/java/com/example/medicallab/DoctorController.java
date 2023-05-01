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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class DoctorController implements Initializable {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField searchBox;
    @FXML
    private ComboBox<String> specialtyFilter;
    @FXML
    private TableView<Doctor> tableView;
    @FXML
    private TableColumn<Doctor, Integer> idColumn;
    @FXML
    private TableColumn<Doctor, String> nameColumn;
    @FXML
    private TableColumn<Doctor, String> emailColumn;
    @FXML
    private TableColumn<Doctor, String> specialtyColumn;
    @FXML
    private TableColumn<Doctor, Double> discountColumn;
    @FXML
    private TableColumn<Doctor, Hyperlink> moreInfoColumn;
    @FXML
    private TableColumn<Doctor, HBox> manipulateColumn;

    // add doctor pane
    @FXML
    private TextField doctorName;
    @FXML
    private TextField doctorSpeciality;
    @FXML
    private TextField discountAmount;
    @FXML
    private TextField doctorEmail;
    @FXML
    private TitledPane addDoctorPane;

    // edit doctors pane
    @FXML
    private AnchorPane editPane;
    @FXML
    private TextField editName;
    @FXML
    private TextField editSpeciality;
    @FXML
    private TextField editDiscount;
    @FXML
    private TextField editEmail;
    @FXML
    private Hyperlink editDoctorLink;
    @FXML
    private Button update;
    @FXML
    private Label supervisedVisitsLabel;
    @FXML
    private Label lastVisitDate;

    private final ObservableList<Doctor> dataList = FXCollections.observableArrayList();
    private final HashMap<String, String> alteredFields = new HashMap<>();
    private final HashMap<String, String> primaryKey = new HashMap<>();
    private final ArrayList<Doctor> tempList = new ArrayList<>();
    private Doctor doctor;
    private String specialty;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Main.loadAllDoctors(dataList);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        for (Doctor d : dataList)
            specialtyFilter.getItems().add(d.getSpecialty());

        FilteredList<Doctor> filteredData = new FilteredList<>(dataList);
        searchBox.setOnKeyTyped(e -> filteredData.setPredicate(doc -> {
            if (searchBox.getText().isEmpty())
                return true;

            String lowerCaseFilter = searchBox.getText().toLowerCase();

            if (doc.getName().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (String.valueOf(doc.getId()).startsWith(lowerCaseFilter))
                return true;
            else
                return false;
        }));
        SortedList<Doctor> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);

        addDoctorPane.setExpanded(false);
        editPane.setVisible(false);
        update.setDisable(true);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));

        moreInfoColumn.setCellFactory(col -> {
            Hyperlink showMoreLink = new Hyperlink("Show More");
            showMoreLink.setStyle("-fx-text-fill: #6666ff;");

            return new TableCell<>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        showMoreLink.setOnMouseClicked(e -> {
                            root.setPrefWidth(1840);
                            editPane.setVisible(true);
                            doctor = getTableView().getItems().get(getIndex());
                            // fill the doctor information
                            fillDoctorData();
                            // set all fields in the edit page to non-editable
                            setAllFieldsEditable(false);

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
                        setGraphic(showMoreLink);
                    }
                }
            };
        });
        manipulateColumn.setCellFactory(col -> {
            Hyperlink edit = new Hyperlink("Edit");
            Hyperlink delete = new Hyperlink("Delete");
            edit.setStyle("-fx-text-fill: #6666ff");
            delete.setStyle("-fx-text-fill: #ff794d;");

            edit.setOnAction(e -> {});

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
                            doctor = getTableView().getItems().get(getIndex());
                            primaryKey.put("doctor_id", String.valueOf(doctor.getId()));
                            try {
                                Main.deleteRecord("Doctor", primaryKey);
                                dataList.remove(doctor);
                            } catch (SQLException exp) {
                                exp.printStackTrace();
                            } finally {
                                primaryKey.clear();
                            }
                        });
                        edit.setOnAction(e -> {
                            root.setPrefWidth(1840);
                            editPane.setVisible(true);
                            doctor = getTableView().getItems().get(getIndex());
                            fillDoctorData();
                            // set the fields in the pane to be editable
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
        editDoctorLink.setOnAction(e -> {
            setAllFieldsEditable(true);
        });
        specialtyFilter.valueProperty().addListener((ov, t, selectedChoice) -> specialty = selectedChoice);
    }

    @FXML
    private void expandPane(MouseEvent e) {
        if (addDoctorPane.isExpanded() && scrollPane.getVvalue() < 0.5) {
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
        else if (!addDoctorPane.isExpanded()){
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
    private void addNewDoctor(ActionEvent e) throws SQLException {
        alteredFields.put("doctor_name", "'" + doctorName.getText() + "'");
        alteredFields.put("specialty", "'" + doctorSpeciality.getText() + "'");
        alteredFields.put("discount", discountAmount.getText());
        alteredFields.put("email", "'" + doctorEmail.getText() + "'");

        try {
            Main.insertDoctor(dataList, alteredFields);
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            alteredFields.clear();
        }
        //add.setDisable(true);
        doctorName.clear();
        doctorSpeciality.clear();
        discountAmount.clear();
        doctorEmail.clear();
    }

    @FXML
    private void add_closeDoctorButton(ActionEvent e) {
        addDoctorPane.setExpanded(!addDoctorPane.isExpanded());
        MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, false, false, false, false, false, false, null);
        addDoctorPane.fireEvent(mouseEvent);
        doctorName.clear();
        doctorSpeciality.clear();
        doctorEmail.clear();
        addDoctorPane.setPrefHeight(470);
    }

    @FXML
    private void updateDoctor(ActionEvent e) {
        String name = editName.getText();
        String spec = editSpeciality.getText();
        String discount = editDiscount.getText();
        String email = editEmail.getText();

        primaryKey.put("doctor_id", String.valueOf(doctor.getId()));

        if (!doctor.getName().equalsIgnoreCase(name)) {
            alteredFields.put("doctor_name", "'" + name + "'");
            doctor.setName(Main.capitalize(name));
        }
        if (!doctor.getSpecialty().equalsIgnoreCase(spec)) {
            alteredFields.put("specialty", "'" + spec + "'");
            doctor.setSpecialty(spec);
        }
        if (!doctor.getEmail().equalsIgnoreCase(email)) {
            alteredFields.put("email", "'" + email + "'");
            doctor.setEmail(email);
        }
        if (doctor.getDiscount() != Double.parseDouble(discount)) {
            alteredFields.put("discount", discount);
            doctor.setDiscount(Double.parseDouble(discount));
        }
        try {
            Main.updateRecord("Doctor", primaryKey, alteredFields);
            tableView.refresh();
            specialtyFilter.getItems().clear();

            for (Doctor d : dataList)
                specialtyFilter.getItems().add(d.getSpecialty());
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            alteredFields.clear();
        }
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
    private void executeFilter(ActionEvent e) {
        try {
            dataList.addAll(tempList);
            tempList.clear();

            List<Integer> ids = Main.filterData("Doctor", "specialty", specialty, '=');

            for (Doctor doc : dataList)
                if (!ids.contains(doc.getId()))
                    tempList.add(doc);

            dataList.removeAll(tempList);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void clearFilter(ActionEvent e) {
        tableView.refresh();
        specialtyFilter.getSelectionModel().clearSelection();
        dataList.addAll(tempList);
        tempList.clear();
    }

    private void fillDoctorData() {
        primaryKey.put("doctor_id", String.valueOf(doctor.getId()));
        editName.setText(doctor.getName());
        editSpeciality.setText(doctor.getSpecialty());
        editDiscount.setText(String.valueOf(doctor.getDiscount()));
        editEmail.setText(doctor.getEmail());
        try {
            int numOfVisits = Main.noOfVisitsFromDoc(doctor.getId());
            supervisedVisitsLabel.setText("Supervised Visits: " + numOfVisits);

            if (numOfVisits > 0) {
                lastVisitDate.setText("Last Visit: " + Main.lastVisitByDoc(doctor.getId()));
            }
            else
                lastVisitDate.setText("No Supervised visits for this Doctor yet!!");
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private void setAllFieldsEditable(boolean editable) {
        editName.setEditable(editable);
        editSpeciality.setEditable(editable);
        editDiscount.setEditable(editable);
        editEmail.setEditable(editable);
        editDoctorLink.setVisible(!editable);
        update.setDisable(!editable);
    }
}
