package com.example.medicallab;

import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.*;

public class PatientController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private TextField searchBar;
    @FXML
    private ChoiceBox<String> filterChoices;
    @FXML
    private DatePicker filterDate;
    @FXML
    private TextField filterField;
    @FXML
    private TableView<Patient> table;
    @FXML
    private TableColumn<Patient, Integer> idColumn;
    @FXML
    private TableColumn<Patient, String> nameColumn;
    @FXML
    private TableColumn<Patient, String> genderColumn;
    @FXML
    private TableColumn<Patient, LocalDate> birthDateColumn;
    @FXML
    private TableColumn<Patient, String> addressColumn;
    @FXML
    private TableColumn<Patient, Hyperlink> contactColumn;
    @FXML
    private TableColumn<Patient, HBox> manipulateColumn;

    // add pane
    @FXML
    private TextField patientName;
    @FXML
    private TextField patientAddress;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField patientEmail;
    @FXML
    private ComboBox<String> yearPicker;
    @FXML
    private ComboBox<String> monthPicker;
    @FXML
    private ComboBox<String> dayPicker;
    @FXML
    private Label invalidDateLabel;
    @FXML
    private RadioButton male;
    @FXML
    private RadioButton female;
    @FXML
    private ToggleGroup genderGroup;
    @FXML
    private CheckBox insuranceBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TitledPane addPatientPane;
    @FXML
    private VBox labelBox;
    @FXML
    private VBox textBox;
    @FXML
    private ButtonBar buttonBar;

    // edit pane
    @FXML
    private AnchorPane editPane;
    @FXML
    private TextField editName;
    @FXML
    private TextField editAddress;
    @FXML
    private TextField editInsurance;
    @FXML
    private DatePicker editDate;
    @FXML
    private TextField editEmail;
    @FXML
    private RadioButton editMale;
    @FXML
    private RadioButton editFemale;
    @FXML
    private ToggleGroup editGenderGroup;
    @FXML
    private Hyperlink editPatientLink;
    @FXML
    private Button update;
    @FXML
    private Label numOfVisitsLabel;
    @FXML
    private Label lastVisit;
    @FXML
    private Label mostVisitedBranch;

    private String gender;
    private char filterChar;
    private int phoneCount = 1, i = 4;
    private int year, month;
    private static Patient patient;
    private final ObservableList<Patient> dataList = FXCollections.observableArrayList();
    private final ArrayList<Patient> tempList = new ArrayList<>();
    // to determine the values that are changed, to change them in the database
    private final HashMap<String, String> alteredFields = new HashMap<>();
    private final HashMap<String, String> primaryKey = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Main.loadAllPatients(dataList);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        filterChoices.getItems().addAll("None", "Birth Date On", "Birth Date Before", "Birth Date After");
        filterChoices.setValue("None");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("patient_id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("patient_name"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("date_of_birth"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        contactColumn.setCellFactory(col -> {
            Hyperlink showMoreLink = new Hyperlink("More Info");
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
                            patient = getTableView().getItems().get(getIndex());
                            // fill the patient information
                            fillPatientData(patient);
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
                            transition.setOnFinished(e2 -> scrollPane.setPadding(new Insets(0, 0, 0, -950)));
                        });
                        setGraphic(showMoreLink);
                    }
                }
            };
        });

        manipulateColumn.setCellFactory(col -> {
            Hyperlink edit = new Hyperlink("Edit");
            Hyperlink delete = new Hyperlink("Delete");
            edit.setStyle("-fx-text-fill: #6666ff;");
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
                            patient = getTableView().getItems().get(getIndex());
                            primaryKey.put("patient_id", String.valueOf(patient.getPatient_id()));
                            try {
                                Main.deleteRecord("Patient", primaryKey);
                                dataList.remove(patient);
                            } catch (SQLException exp) {
                                exp.printStackTrace();
                            } finally {
                                primaryKey.clear();
                            }
                        });
                        edit.setOnAction(e -> {
                            root.setPrefWidth(1840);
                            editPane.setVisible(true);
                            patient = getTableView().getItems().get(getIndex());
                            fillPatientData(patient);
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
                            transition.setOnFinished(e2 -> scrollPane.setPadding(new Insets(0, 0, 0, -950)));
                        });
                    }
                }
            };
        });
        FilteredList<Patient> filteredData = new FilteredList<>(dataList);
        searchBar.setOnKeyTyped(e -> filteredData.setPredicate(patient -> {
            if (searchBar.getText().isEmpty())
                return true;

            String lowerCaseFilter = searchBar.getText().toLowerCase();

            if (patient.getPatient_name().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (patient.getGender().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (patient.getAddress().toLowerCase().contains(lowerCaseFilter))
                return true;
            else if (String.valueOf(patient.getPatient_id()).contains(lowerCaseFilter))
                return true;
            else
                return false;
        }));
        SortedList<Patient> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
        // handling the filtering of the comboBox
        filterChoices.valueProperty().addListener((ov, t, selectedChoice) -> {
            if (selectedChoice.equalsIgnoreCase("Birth Date on")) {
                filterField.setVisible(false);
                filterDate.setVisible(true);
                filterChar = '=';
            }
            else if (selectedChoice.equalsIgnoreCase("birth date before")) {
                filterField.setVisible(false);
                filterDate.setVisible(true);
                filterChar = '<';
            }
            else if (selectedChoice.equalsIgnoreCase("birth date after")) {
                filterField.setVisible(false);
                filterDate.setVisible(true);
                filterChar = '>';
            }
            else if (selectedChoice.equalsIgnoreCase("None")) {
                filterField.setVisible(false);
                filterDate.setVisible(false);
                filterDate.getEditor().clear();
                filterField.clear();
            }
            dataList.addAll(tempList);
            tempList.clear();
            filterDate.getEditor().clear();
            table.refresh();
        });
        addPatientPane.setExpanded(false);

        Calendar calendar = Calendar.getInstance();

        for (int y = calendar.get(Calendar.YEAR); y >= 1940; y--)
            yearPicker.getItems().add(Integer.toString(y));

        // add a listener for comboBoxes
        yearPicker.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                monthPicker.getItems().clear();
                dayPicker.getItems().clear();
                String[] months = new DateFormatSymbols().getMonths();

                for (String m : months)
                    monthPicker.getItems().add(m);
            }
        });
        monthPicker.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                dayPicker.getItems().clear();
                month = monthPicker.getSelectionModel().getSelectedIndex();
                year = Integer.parseInt(yearPicker.getValue());
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int d = 1; d <= maxDays; d++)
                    dayPicker.getItems().add(String.valueOf(d));
            }
        });
        male.selectedProperty().addListener(observable -> gender = "Male");
        female.selectedProperty().addListener(observable -> gender = "Female");
        editMale.selectedProperty().addListener(observable -> gender = "Male");
        editFemale.selectedProperty().addListener(observable -> gender = "Female");
    }

    @FXML
    private void filterBy(ActionEvent e) throws SQLException {
        dataList.addAll(tempList);
        tempList.clear();

        String filterValue = java.sql.Date.valueOf(filterDate.getValue()).toString();
        List<Integer> ids = Main.filterData("Patient", "date_of_birth", filterValue, filterChar);

        for (Patient value : dataList)
            if (!ids.contains(value.getPatient_id()))
                tempList.add(value);

        dataList.removeAll(tempList);
    }

    @FXML
    private void expandPane(MouseEvent e) {
        if (addPatientPane.isExpanded() && scrollPane.getVvalue() < 0.5) {
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
        else if (!addPatientPane.isExpanded()){
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
    private void add_closePatientButton(ActionEvent e) {
        addPatientPane.setExpanded(!addPatientPane.isExpanded());
        MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, false, false, false, false, false, false, null);
        addPatientPane.fireEvent(mouseEvent);
        male.setSelected(false);
        female.setSelected(false);
        patientName.clear();
        yearPicker.getSelectionModel().clearSelection();
        monthPicker.getSelectionModel().clearSelection();
        dayPicker.getSelectionModel().clearSelection();
        invalidDateLabel.setText("");
        patientAddress.clear();
        phoneNumber.clear();
        patientEmail.clear();
        insuranceBox.setSelected(false);

        for (; i >= 5; i--) {
            labelBox.getChildren().remove(i);
            textBox.getChildren().remove(i);
        }
        buttonBar.setLayoutY(390);
        addPatientPane.setPrefHeight(470);
    }

    @FXML
    private void addNewPatient(ActionEvent e) throws SQLException {
        String name = patientName.getText();
        LocalDate date = LocalDate.of(year, month+1, dayPicker.getSelectionModel().getSelectedIndex()+1);
        String address = patientAddress.getText();
        String email = patientEmail.getText();
        boolean hasInsurance = insuranceBox.isSelected();
        ArrayList<String> phones = new ArrayList<>();
        phones.add(phoneNumber.getText());

        for (int j = 5; j <= i; j++) {
            TextField field = (TextField) ((HBox)textBox.getChildren().get(j)).getChildren().get(0);
            phones.add(field.getText());
            field.clear();
        }
        textBox.getChildren().remove(5, i+1);
        labelBox.getChildren().remove(5, i+1);

        if (!date.isAfter(LocalDate.now())) {
            Main.insertPatient(dataList, name, date, gender, email, address, hasInsurance, phones);
            invalidDateLabel.setText("");
        }
        else
            invalidDateLabel.setText("THE ENTERED DATE IS INVALID\nPLEASE DOUBLE CHECK YOUR INPUT");

        patientName.clear();
        // clear the selection
        yearPicker.getSelectionModel().clearSelection();
        monthPicker.getSelectionModel().clearSelection();
        dayPicker.getSelectionModel().clearSelection();
        patientAddress.clear();
        phoneNumber.clear();
        patientEmail.clear();
        male.setSelected(false);
        female.setSelected(false);
        insuranceBox.setSelected(false);
        gender = "";
        phoneCount = 1;
        i = 4;
    }

    @FXML
    private void addAnotherPhone(ActionEvent e) {
        Label label = new Label("Phone Number " + (++phoneCount) + ":");
        label.setStyle("-fx-font-size: 18px");
        HBox phoneBox = new HBox();
        Button remove = new Button("del");

        phoneBox.getChildren().addAll(new TextField(), remove);

        labelBox.getChildren().add(++i, label);
        textBox.getChildren().add(i, phoneBox);
        addPatientPane.setPrefHeight(addPatientPane.getPrefHeight() + 50);
        root.setPrefHeight(root.getPrefHeight() + 50);
        buttonBar.setLayoutY(buttonBar.getLayoutY() + 50);

        remove.setOnAction(event -> {
            labelBox.getChildren().remove(i);
            textBox.getChildren().remove(i--);
            phoneCount--;
            addPatientPane.setPrefHeight(addPatientPane.getPrefHeight() - 50);
            buttonBar.setLayoutY(buttonBar.getLayoutY() - 50);
            root.setPrefHeight(root.getPrefHeight() - 50);
            scrollPane.setVvalue(0);
        });
    }

    @FXML
    private void editThisPatient(ActionEvent e) {
        setAllFieldsEditable(true);
    }

    @FXML
    private void updateButton(ActionEvent e) {
        String name = editName.getText();
        LocalDate date = editDate.getValue();
        String address = editAddress.getText();
        String email = editEmail.getText();
        boolean insurance = editInsurance.getText().equalsIgnoreCase("private");

        if (!patient.getPatient_name().equalsIgnoreCase(name)) {
            alteredFields.put("patient_name", "'" + name + "'");
            patient.setPatient_name(Main.capitalize(name));
        }
        if (!patient.getAddress().equalsIgnoreCase(address)) {
            alteredFields.put("address", "'" + address + "'");
            patient.setAddress(address);
        }
        if (!patient.getEmail().equalsIgnoreCase(email)) {
            alteredFields.put("email", "'" + email + "'");
            patient.setEmail(email);
        }
        if (!patient.getGender().equalsIgnoreCase(gender)) {
            alteredFields.put("gender", "'" + gender + "'");
            patient.setGender(gender);
        }
        if (patient.getDate_of_birth() != date) {
            alteredFields.put("date_of_birth", "'" + date.toString() + "'");
            patient.setDate_of_birth(date);
        }
        if (insurance != patient.getHasInsurance()) {
            alteredFields.put("hasInsurance", String.valueOf(insurance));
            patient.setHasInsurance(insurance);
        }
        try {
            Main.updateRecord("Patient", primaryKey, alteredFields);
            table.refresh();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
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
    private void openEditPhonesPage(ActionEvent e) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit-patient-phones.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 280, 307);

        stage.setTitle("Edit Patient Phones");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    private void fillPatientData(Patient p) {
        primaryKey.put("patient_id", String.valueOf(p.getPatient_id()));
        editName.setText(p.getPatient_name());

        if (p.getGender().equalsIgnoreCase("male")) {
            editMale.setSelected(true);
            editFemale.setSelected(false);
        }
        else if (p.getGender().equalsIgnoreCase("female")) {
            editFemale.setSelected(true);
            editMale.setSelected(false);
        }
        editDate.setValue(p.getDate_of_birth());
        editAddress.setText(p.getAddress());
        editInsurance.setText((p.getHasInsurance())? "Private": "None");
        editEmail.setText(p.getEmail());

        try {
            numOfVisitsLabel.setText("Total Visits: " + Main.noOfPatientVisits(patient.getPatient_id()));
            String[] s = Main.lastVisitByPatient(patient.getPatient_id()).split(",");
            String b = Main.patientMostVisitedBranch(patient.getPatient_id());

            if (s.length > 1)
                lastVisit.setText("Last Visit Made on: " + s[0] + ", Visit ID: " + s[1]);
            else {
                lastVisit.setText("No Visits Are Made!!!");
                mostVisitedBranch.setText("No Visits Are Made!!!");
            }
            if (b != null)
                mostVisitedBranch.setText("Most Visited Branch: " + b);
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    private void setAllFieldsEditable(boolean editable) {
        editName.setEditable(editable);
        editAddress.setEditable(editable);
        editEmail.setEditable(editable);
        editDate.setEditable(editable);
        editInsurance.setEditable(editable);
        editFemale.setDisable(!editable);
        editMale.setDisable(!editable);
        editPatientLink.setVisible(!editable);
        update.setDisable(!editable);
    }

    public static Patient getPatient() {
        return patient;
    }
}// end class